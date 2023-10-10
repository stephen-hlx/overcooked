package overcooked.core.action;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.*;
import overcooked.sample.diehard.model.Jar5;
import overcooked.sample.diehard.modelverifier.Jar3State;
import overcooked.sample.diehard.modelverifier.Jar5State;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class IntransitiveActionTemplateExecutorTest {
    private final IntransitiveActionTaker intransitiveActionTaker = mock(IntransitiveActionTaker.class);

    @Test
    void when_provided_with_a_transitive_action_then_throws_illegalArgumentException() {
        IntransitiveActionTemplateExecutor executor = IntransitiveActionTemplateExecutor.builder().build();
        assertThatThrownBy(
            () -> executor.execute(
                null,
                null,
                ActionTemplate.builder()
                    .actionType(new TransitiveActionType(null))
                    .build()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageStartingWith("Expecting an intransitive action template but it was transitive");
    }

    @Test
    void execute_calls_intransitive_action_taker_and_converts_actor_back_to_local_state() {
        Jar5 actor = new Jar5(0);
        LocalState actorLocalState = new Jar3State(0);
        LocalState newActorLocalState = new Jar3State(1);
        ActorDefinition actorDefinition = ActorDefinition.builder()
            .id("jar5")
            .type(Jar5.class)
            .localStateType(Jar5State.class)
            .build();

        ActionTemplate actionTemplate = ActionTemplate.builder()
            .actionType(new IntransitiveActionType())
            .methodName("fill - but doesn't really matter in this test")
            .build();

        @SuppressWarnings("unchecked")
        ActorFactory<Jar5> actorFactory = mock(ActorFactory.class);
        when(actorFactory.restoreFromLocalState(actorLocalState)).thenReturn(actor);

        LocalStateExtractor actorLocalStateExtractor = mock(LocalStateExtractor.class);
        when(actorLocalStateExtractor.extract(actor)).thenReturn(newActorLocalState);

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
            .isEqualTo(ImmutableMap.of(
                actorDefinition, newActorLocalState
            ));

        verify(actorFactory).restoreFromLocalState(actorLocalState);
        verify(intransitiveActionTaker).take(IntransitiveAction.builder()
            .actor(actor)
            .actionTemplate(actionTemplate)
            .build());
        verify(actorLocalStateExtractor).extract(actor);

        verifyNoMoreInteractions(
            actorFactory,
            actorLocalStateExtractor);
    }
}