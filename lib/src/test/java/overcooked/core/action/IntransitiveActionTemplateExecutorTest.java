package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import overcooked.core.actor.ActorBase;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateExtractor;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;
import overcooked.util.TestActorState;

class IntransitiveActionTemplateExecutorTest {
  private static final TestActor ACTION_PERFORMER = new TestActor();
  private static final ActorId NOT_USED_ACTOR_ID = new ActorId("notUsed");

  private final ActionTaker actionTaker = mock(ActionTaker.class);
  private final FailureRecordingOverrider failureRecordingOverrider =
      mock(FailureRecordingOverrider.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<TestActor> actorFactory = mock(ActorFactory.class);
  @SuppressWarnings("unchecked")
  private final ActorStateExtractor<TestActor> actorStateExtractor =
      mock(ActorStateExtractor.class);
  private final InOrder inOrder = Mockito.inOrder(actionTaker,
      actorFactory,
      failureRecordingOverrider,
      actorStateExtractor);

  @Test
  void when_provided_with_a_transitive_action_then_throws_illegalArgumentException() {
    IntransitiveActionTemplateExecutor executor =
        IntransitiveActionTemplateExecutor.builder().build();
    assertThatThrownBy(
        () -> executor.execute(
            ActionTemplate.builder()
                .actionType(new TransitiveActionType(NOT_USED_ACTOR_ID))
                .actionPerformerId(NOT_USED_ACTOR_ID)
                .actionLabel("not used")
                .action((notUsed1, notUsed2) -> {})
                .build(),
            LocalState.builder().build()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Expecting an intransitive action template but it was transitive");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void execute_calls_intransitive_action_taker_and_converts_actor_back_to_local_state() {
    TestActorState actorState = new TestActorState(0, 0);
    LocalState localState = LocalState.builder()
        .actorState(actorState)
        .build();
    ActorState newActorState = new TestActorState(1, 1);
    ActorId actionPerformerId = new ActorId("actor");

    ActionTemplate<TestActor, Void> actionTemplate = ActionTemplate.<TestActor, Void>builder()
        .actionPerformerId(actionPerformerId)
        .actionType(new IntransitiveActionType())
        .actionLabel("not used")
        .action((notUsed1, notUsed2) -> {})
        .build();
    ActionDefinition<TestActor, Void> actionDefinition = ActionDefinition.<TestActor, Void>builder()
        .action(actionTemplate.getAction())
        .actionPerformer(ACTION_PERFORMER)
        .actionReceiver(null)
        .actionLabel("not used")
        .build();

    when(actorFactory.restoreFromActorState(actorState)).thenReturn(ACTION_PERFORMER);

    when(actorStateExtractor.extract(ACTION_PERFORMER)).thenReturn(newActorState);
    when(actionTaker.take(actionDefinition)).thenReturn(ActionResult.success());
    doAnswer(invocation -> invocation.getArgument(0))
        .when(failureRecordingOverrider)
        .override(any(TestActor.class), anyMap());

    IntransitiveActionTemplateExecutor executor = IntransitiveActionTemplateExecutor.builder()
        .failureRecordingOverrider(failureRecordingOverrider)
        .config(ActorStateTransformerConfig.builder()
            .actorFactories(ImmutableMap.of(actionPerformerId, actorFactory))
            .actorStateExtractors(ImmutableMap.of(
                actionPerformerId, actorStateExtractor))
            .build())
        .actionTaker(actionTaker)
        .build();

    assertThat(executor.execute(
        actionTemplate,
        localState))
        .isEqualTo(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(actionPerformerId, LocalState.builder()
                .actorState(newActorState)
                .build()))
            .build());

    inOrder.verify(actorFactory).restoreFromActorState(actorState);
    inOrder.verify(failureRecordingOverrider).override(eq(ACTION_PERFORMER), anyMap());
    inOrder.verify(actionTaker).take(actionDefinition);
    inOrder.verify(actorStateExtractor).extract(ACTION_PERFORMER);

    inOrder.verifyNoMoreInteractions();
  }

  @EqualsAndHashCode
  private static class TestActor implements ActorBase {
  }
}