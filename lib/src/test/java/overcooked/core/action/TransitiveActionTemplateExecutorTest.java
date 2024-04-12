package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateExtractor;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.util.TestActorState;

class TransitiveActionTemplateExecutorTest {
  private static final String ACTION_PERFORMER = "actionPerformerObject";
  private static final String ACTION_RECEIVER = "actionReceiverObject";
  private final ActionTaker actionTaker = mock(ActionTaker.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<String> actionPerformerFactory = mock(ActorFactory.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<String> actionReceiverFactory = mock(ActorFactory.class);
  @SuppressWarnings("unchecked")
  private final ActorStateExtractor<String> actionPerformerStateExtractor =
      mock(ActorStateExtractor.class);
  @SuppressWarnings("unchecked")
  private final ActorStateExtractor<String> actionReceiverStateExtractor =
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
            null,
            null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Expecting a transitive action template but it was intransitive");
  }

  @Test
  void execute_calls_transitive_action_taker_and_converts_actors_back_to_local_state() {
    ActorState actionPerformerState = new TestActorState(0, 0);
    ActorState newActionPerformerState = new TestActorState(0, 1);
    ActorId actionPerformerId = new ActorId("actionPerformer");

    ActorState actionReceiverState = new TestActorState(1, 0);
    ActorState newActionReceiverState = new TestActorState(1, 1);
    ActorId actionReceiverId = new ActorId("actionReceiver");

    ActionTemplate<String, String> actionTemplate = ActionTemplate.<String, String>builder()
        .actionPerformerId(actionPerformerId)
        .actionType(new TransitiveActionType(actionReceiverId))
        .actionLabel("not used")
        .action((notUsed1, notUsed2) -> {})
        .build();
    ActionDefinition<String, String> actionDefinition = ActionDefinition.<String, String>builder()
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
        .build();

    assertThat(executor.execute(
        actionTemplate,
        actionPerformerState,
        actionReceiverState))
        .isEqualTo(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(
                actionPerformerId, newActionPerformerState,
                actionReceiverId, newActionReceiverState
            )).build());

    verify(actionPerformerFactory).restoreFromActorState(actionPerformerState);
    verify(actionReceiverFactory).restoreFromActorState(actionReceiverState);
    verify(actionTaker).take(actionDefinition);
    verify(actionPerformerStateExtractor).extract(ACTION_PERFORMER);
    verify(actionReceiverStateExtractor).extract(ACTION_RECEIVER);

    verifyNoMoreInteractions(
        actionTaker,
        actionReceiverFactory,
        actionPerformerFactory,
        actionPerformerStateExtractor,
        actionReceiverStateExtractor);
  }
}