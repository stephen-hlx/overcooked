package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorBase;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateExtractor;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;
import overcooked.util.TestActorState;

class TransitiveActionTemplateExecutorTest {
  private static final TestActionPerformer ACTION_PERFORMER = new TestActionPerformer();
  private static final TestActionReceiver ACTION_RECEIVER = new TestActionReceiver();
  private final ActionTaker actionTaker = mock(ActionTaker.class);
  private final FailureInjector failureInjector = mock(FailureInjector.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<TestActionPerformer> actionPerformerFactory = mock(ActorFactory.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<TestActionReceiver> actionReceiverFactory = mock(ActorFactory.class);
  @SuppressWarnings("unchecked")
  private final ActorStateExtractor<TestActionPerformer> actionPerformerStateExtractor =
      mock(ActorStateExtractor.class);
  @SuppressWarnings("unchecked")
  private final ActorStateExtractor<TestActionReceiver> actionReceiverStateExtractor =
      mock(ActorStateExtractor.class);

  @Test
  void when_provided_with_an_intransitive_action_then_throws_illegalArgumentException() {
    TransitiveActionTemplateExecutor executor = TransitiveActionTemplateExecutor.builder().build();
    assertThatThrownBy(
        () -> executor.execute(
            ActionTemplate.builder()
                .actionType(new IntransitiveActionType())
                .actionPerformerId(new ActorId("notUsed"))
                .actionLabel("not used")
                .action((notUsed1, notUsed2) -> {})
                .build(),
            LocalState.builder().build(),
            LocalState.builder().build()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Expecting a transitive action template but it was intransitive");
  }

  @Test
  void execute_calls_transitive_action_taker_and_converts_actors_back_to_local_state() {
    ActorState actionPerformerState = new TestActorState(0, 0);
    LocalState actionPerformerLocalState = LocalState.builder()
        .actorState(actionPerformerState)
        .build();
    ActorState newActionPerformerState = new TestActorState(0, 1);
    ActorId actionPerformerId = new ActorId("actionPerformer");

    ActorState actionReceiverState = new TestActorState(1, 0);
    LocalState actionReceiverLocalState = LocalState.builder()
        .actorState(actionReceiverState)
        .build();
    ActorState newActionReceiverState = new TestActorState(1, 1);
    ActorId actionReceiverId = new ActorId("actionReceiver");

    ActionTemplate<TestActionPerformer, TestActionReceiver> actionTemplate =
        ActionTemplate.<TestActionPerformer, TestActionReceiver>builder()
            .actionPerformerId(actionPerformerId)
            .actionType(new TransitiveActionType(actionReceiverId))
            .actionLabel("not used")
            .action((notUsed1, notUsed2) -> {
            })
            .build();
    ActionDefinition<TestActionPerformer, TestActionReceiver> actionDefinition =
        ActionDefinition.<TestActionPerformer, TestActionReceiver>builder()
            .action(actionTemplate.getAction())
            .actionReceiver(ACTION_RECEIVER)
            .actionPerformer(ACTION_PERFORMER)
            .actionLabel("not used")
            .build();

    when(actionPerformerFactory.restoreFromActorState(actionPerformerState))
        .thenReturn(ACTION_PERFORMER);
    when(actionReceiverFactory.restoreFromActorState(actionReceiverState))
        .thenReturn(ACTION_RECEIVER);

    when(actionPerformerStateExtractor.extract(ACTION_PERFORMER))
        .thenReturn(newActionPerformerState);
    when(actionReceiverStateExtractor.extract(ACTION_RECEIVER))
        .thenReturn(newActionReceiverState);
    when(actionTaker.take(actionDefinition))
        .thenReturn(ActionResult.success());
    doAnswer(invocation -> invocation.getArgument(0))
        .when(failureInjector)
        .inject(eq(ACTION_RECEIVER), any());

    TransitiveActionTemplateExecutor executor = TransitiveActionTemplateExecutor.builder()
        .config(ActorStateTransformerConfig.builder()
            .actorFactories(ImmutableMap.of(
                actionPerformerId, actionPerformerFactory,
                actionReceiverId, actionReceiverFactory
            ))
            .actorStateExtractors(ImmutableMap.of(
                actionPerformerId, actionPerformerStateExtractor,
                actionReceiverId, actionReceiverStateExtractor
            ))
            .build())
        .actionTaker(actionTaker)
        .failureInjector(failureInjector)
        .build();

    assertThat(executor.execute(
        actionTemplate,
        actionPerformerLocalState,
        actionReceiverLocalState))
        .isEqualTo(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(
                actionPerformerId, LocalState.builder()
                    .actorState(newActionPerformerState)
                    .build(),
                actionReceiverId, LocalState.builder()
                    .actorState(newActionReceiverState)
                    .build()
            )).build());

    verify(actionPerformerFactory).restoreFromActorState(actionPerformerState);
    verify(actionReceiverFactory).restoreFromActorState(actionReceiverState);
    verify(failureInjector).inject(eq(ACTION_RECEIVER), any());
    verify(actionTaker).take(actionDefinition);
    verify(actionPerformerStateExtractor).extract(ACTION_PERFORMER);
    verify(actionReceiverStateExtractor).extract(ACTION_RECEIVER);

    verifyNoMoreInteractions(
        actionTaker,
        failureInjector,
        actionReceiverFactory,
        actionPerformerFactory,
        actionPerformerStateExtractor,
        actionReceiverStateExtractor);
  }

  @EqualsAndHashCode
  private static class TestActionPerformer implements ActorBase {
  }

  @EqualsAndHashCode
  private static class TestActionReceiver implements ActorBase {
  }
}