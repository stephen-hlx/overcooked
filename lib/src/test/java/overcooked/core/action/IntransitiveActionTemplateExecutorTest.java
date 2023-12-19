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
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.util.TestLocalState;

class IntransitiveActionTemplateExecutorTest {
  private static final Integer ACTION_PERFORMER = 0;
  private final ActionTaker actionTaker = mock(ActionTaker.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<Integer> actorFactory = mock(ActorFactory.class);
  @SuppressWarnings("unchecked")
  private final LocalStateExtractor<Integer> actorLocalStateExtractor =
      mock(LocalStateExtractor.class);
  private final InOrder inOrder = Mockito.inOrder(actionTaker, actorFactory,
      actorLocalStateExtractor);

  @Test
  void when_provided_with_a_transitive_action_then_throws_illegalArgumentException() {
    IntransitiveActionTemplateExecutor executor =
        IntransitiveActionTemplateExecutor.builder().build();
    assertThatThrownBy(
        () -> executor.execute(
            ActionTemplate.builder()
                .actionType(new TransitiveActionType(ActorId.builder().id("notUsed").build()))
                .actionPerformerId(ActorId.builder().id("notUsed").build())
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
    LocalState actorLocalState = new TestLocalState(0, 0);
    LocalState newActorLocalState = new TestLocalState(1, 1);
    ActorId actionPerformerId = ActorId.builder().id("actor").build();

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

    when(actorFactory.restoreFromLocalState(actorLocalState)).thenReturn(ACTION_PERFORMER);

    when(actorLocalStateExtractor.extract(ACTION_PERFORMER)).thenReturn(newActorLocalState);
    when(actionTaker.take(actionDefinition)).thenReturn(ActionResult.success());

    IntransitiveActionTemplateExecutor executor = IntransitiveActionTemplateExecutor.builder()
        .config(ActorStateTransformerConfig.builder()
            .actorFactories(ImmutableMap.of(actionPerformerId, actorFactory))
            .localStateExtractors(ImmutableMap.of(
                actionPerformerId, actorLocalStateExtractor))
            .build())
        .actionTaker(actionTaker)
        .build();

    assertThat(executor.execute(
        actionTemplate,
        actorLocalState))
        .isEqualTo(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(actionPerformerId, newActorLocalState))
            .build());

    inOrder.verify(actorFactory).restoreFromLocalState(actorLocalState);
    inOrder.verify(actionTaker).take(actionDefinition);
    inOrder.verify(actorLocalStateExtractor).extract(ACTION_PERFORMER);

    inOrder.verifyNoMoreInteractions();
  }
}