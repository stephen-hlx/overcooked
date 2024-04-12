package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateExtractor;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.util.TestActorState;

class IntransitiveActionTemplateExecutorTest {
  private static final Integer ACTION_PERFORMER = 0;
  private static final ActorId NOT_USED_ACTOR_ID = new ActorId("notUsed");

  private final ActionTaker actionTaker = mock(ActionTaker.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<Integer> actorFactory = mock(ActorFactory.class);
  @SuppressWarnings("unchecked")
  private final ActorStateExtractor<Integer> actorStateExtractor =
      mock(ActorStateExtractor.class);
  private final InOrder inOrder = Mockito.inOrder(actionTaker, actorFactory,
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
            null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Expecting an intransitive action template but it was transitive");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void execute_calls_intransitive_action_taker_and_converts_actor_back_to_local_state() {
    ActorState actorState = new TestActorState(0, 0);
    ActorState newActorState = new TestActorState(1, 1);
    ActorId actionPerformerId = new ActorId("actor");

    ActionTemplate<Integer, Void> actionTemplate = ActionTemplate.<Integer, Void>builder()
        .actionPerformerId(actionPerformerId)
        .actionType(new IntransitiveActionType())
        .actionLabel("not used")
        .action((notUsed1, notUsed2) -> {})
        .build();
    ActionDefinition<Integer, Void> actionDefinition = ActionDefinition.<Integer, Void>builder()
        .action(actionTemplate.getAction())
        .actionPerformer(ACTION_PERFORMER)
        .actionReceiver(null)
        .actionLabel("not used")
        .build();

    when(actorFactory.restoreFromActorState(actorState)).thenReturn(ACTION_PERFORMER);

    when(actorStateExtractor.extract(ACTION_PERFORMER)).thenReturn(newActorState);
    when(actionTaker.take(actionDefinition)).thenReturn(ActionResult.success());

    IntransitiveActionTemplateExecutor executor = IntransitiveActionTemplateExecutor.builder()
        .config(ActorStateTransformerConfig.builder()
            .actorFactories(ImmutableMap.of(actionPerformerId, actorFactory))
            .actorStateExtractors(ImmutableMap.of(
                actionPerformerId, actorStateExtractor))
            .build())
        .actionTaker(actionTaker)
        .build();

    assertThat(executor.execute(
        actionTemplate,
        actorState))
        .isEqualTo(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(actionPerformerId, newActorState))
            .build());

    inOrder.verify(actorFactory).restoreFromActorState(actorState);
    inOrder.verify(actionTaker).take(actionDefinition);
    inOrder.verify(actorStateExtractor).extract(ACTION_PERFORMER);

    inOrder.verifyNoMoreInteractions();
  }
}