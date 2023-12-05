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
import overcooked.sample.diehard.model.Jar5;
import overcooked.sample.diehard.modelverifier.Jar3State;

class IntransitiveActionTemplateExecutorTest {
  private final IntransitiveActionTaker intransitiveActionTaker =
      mock(IntransitiveActionTaker.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<Jar5> actorFactory = mock(ActorFactory.class);
  private final LocalStateExtractor actorLocalStateExtractor = mock(LocalStateExtractor.class);
  private final InOrder inOrder = Mockito.inOrder(intransitiveActionTaker,
      actorFactory, actorLocalStateExtractor);

  @Test
  void when_provided_with_a_transitive_action_then_throws_illegalArgumentException() {
    IntransitiveActionTemplateExecutor executor =
        IntransitiveActionTemplateExecutor.builder().build();
    assertThatThrownBy(
        () -> executor.execute(
            null,
            null,
            ActionTemplate.builder()
                .actionType(new TransitiveActionType(null))
                .build()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Expecting an intransitive action template but it was transitive");
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void execute_calls_intransitive_action_taker_and_converts_actor_back_to_local_state() {
    Jar5 actor = new Jar5(0);
    LocalState actorLocalState = new Jar3State(0);
    LocalState newActorLocalState = new Jar3State(1);
    Actor actorDefinition = Actor.builder()
        .id("jar5")
        .build();

    ActionTemplate actionTemplate = ActionTemplate.builder()
        .actionType(new IntransitiveActionType())
        .methodName("fill - but doesn't really matter in this test")
        .build();

    when(actorFactory.restoreFromLocalState(actorLocalState)).thenReturn(actor);

    when(actorLocalStateExtractor.extract(actor)).thenReturn(newActorLocalState);
    when(intransitiveActionTaker.take(IntransitiveAction.builder()
            .actionTemplate(actionTemplate)
            .actor(actor)
        .build()))
        .thenReturn(ActionResult.success());

    IntransitiveActionTemplateExecutor executor = IntransitiveActionTemplateExecutor.builder()
        .config(ActorStateTransformerConfig.builder()
            .actorFactories(ImmutableMap.of(
                actorDefinition, actorFactory
            ))
            .localStateExtractors(ImmutableMap.of(
                actorDefinition, actorLocalStateExtractor
            ))
            .build())
        .intransitiveActionTaker(intransitiveActionTaker)
        .build();

    assertThat(executor.execute(
        actorLocalState,
        actorDefinition,
        actionTemplate))
        .isEqualTo(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(
                actorDefinition, newActorLocalState
            ))
            .build());

    inOrder.verify(actorFactory).restoreFromLocalState(actorLocalState);
    inOrder.verify(intransitiveActionTaker).take(IntransitiveAction.builder()
        .actor(actor)
        .actionTemplate(actionTemplate)
        .build());
    inOrder.verify(actorLocalStateExtractor).extract(actor);

    inOrder.verifyNoMoreInteractions();
  }
}