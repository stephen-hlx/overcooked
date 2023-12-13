package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import overcooked.core.actor.Actor;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.util.TestLocalState;

class IntransitiveActionTemplateExecutorTest {
  private static final Integer ACTOR = 0;
  private final IntransitiveActionTaker intransitiveActionTaker =
      mock(IntransitiveActionTaker.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<Integer> actorFactory = mock(ActorFactory.class);
  @SuppressWarnings("unchecked")
  private final LocalStateExtractor<Integer> actorLocalStateExtractor =
      mock(LocalStateExtractor.class);
  private final InOrder inOrder = Mockito.inOrder(intransitiveActionTaker,
      actorFactory, actorLocalStateExtractor);

  @Test
  void when_provided_with_a_transitive_action_then_throws_illegalArgumentException() {
    IntransitiveActionTemplateExecutor executor =
        IntransitiveActionTemplateExecutor.builder().build();
    assertThatThrownBy(
        () -> executor.execute(
            ActionTemplate.builder()
                .actionType(new TransitiveActionType(null))
                .actionPerformerDefinition(Actor.builder().id("notUsed").build())
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
    Actor actionPerformerDefinition = Actor.builder().id("actor").build();

    ActionTemplate<Integer, Void> actionTemplate = ActionTemplate.<Integer, Void>builder()
        .actionPerformerDefinition(actionPerformerDefinition)
        .actionType(new IntransitiveActionType())
        .actionLabel("not used")
        .action((notUsed1, notUsed2) -> {})
        .build();

    when(actorFactory.restoreFromLocalState(actorLocalState)).thenReturn(ACTOR);

    when(actorLocalStateExtractor.extract(ACTOR)).thenReturn(newActorLocalState);
    when(intransitiveActionTaker.take(IntransitiveAction.<Integer, Void>builder()
            .actionTemplate(actionTemplate)
            .actor(ACTOR)
        .build()))
        .thenReturn(ActionResult.success());

    IntransitiveActionTemplateExecutor executor = IntransitiveActionTemplateExecutor.builder()
        .config(ActorStateTransformerConfig.builder()
            .actorFactories(ImmutableMap.of(actionPerformerDefinition, actorFactory))
            .localStateExtractors(ImmutableMap.of(
                actionPerformerDefinition, actorLocalStateExtractor))
            .build())
        .intransitiveActionTaker(intransitiveActionTaker)
        .build();

    assertThat(executor.execute(
        actionTemplate,
        actorLocalState))
        .isEqualTo(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(actionPerformerDefinition, newActorLocalState))
            .build());

    inOrder.verify(actorFactory).restoreFromLocalState(actorLocalState);
    inOrder.verify(intransitiveActionTaker).take(IntransitiveAction.<Integer, Void>builder()
        .actor(ACTOR)
        .actionTemplate(actionTemplate)
        .build());
    inOrder.verify(actorLocalStateExtractor).extract(ACTOR);

    inOrder.verifyNoMoreInteractions();
  }
}