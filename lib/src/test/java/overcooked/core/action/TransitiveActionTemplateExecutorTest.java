package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.Actor;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.sample.diehard.model.Jar3;
import overcooked.sample.diehard.model.Jar5;
import overcooked.sample.diehard.modelverifier.Jar3State;
import overcooked.sample.diehard.modelverifier.Jar5State;

class TransitiveActionTemplateExecutorTest {
  private final TransitiveActionTaker transitiveActionTaker = mock(TransitiveActionTaker.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<Jar3> jar3ActorFactory = mock(ActorFactory.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<Jar5> jar5ActorFactory = mock(ActorFactory.class);
  private final LocalStateExtractor actionPerformerLocalStateExtractor =
      mock(LocalStateExtractor.class);
  private final LocalStateExtractor actionReceiverLocalStateExtractor =
      mock(LocalStateExtractor.class);

  @Test
  void when_provided_with_an_intransitive_action_then_throws_illegalArgumentException() {
    TransitiveActionTemplateExecutor executor = TransitiveActionTemplateExecutor.builder().build();
    assertThatThrownBy(
        () -> executor.execute(
            null,
            null,
            null,
            ActionTemplate.builder()
                .actionType(new IntransitiveActionType())
                .build()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Expecting a transitive action template but it was intransitive");
  }

  @Test
  void execute_calls_transitive_action_taker_and_converts_actors_back_to_local_state() {
    Jar5 actionPerformer = new Jar5(0);
    LocalState actionPerformerLocalState = new Jar5State(0);
    LocalState newActionPerformerLocalState = new Jar5State(1);
    Actor actionPerformerDefinition = Actor.builder()
        .id("jar5")
        .build();

    Jar3 actionReceiver = new Jar3(0);
    LocalState actionReceiverLocalState = new Jar3State(0);
    LocalState newActionReceiverLocalState = new Jar3State(1);
    Actor actionReceiverDefinition = Actor.builder()
        .id("jar3")
        .build();

    ActionTemplate actionTemplate = ActionTemplate.builder()
        .actionType(new TransitiveActionType(actionReceiverDefinition))
        .methodName("addTo - but doesn't really matter in this test")
        .parameters(ImmutableList.of(new ParamTemplate<>(Jar3.class)))
        .build();

    when(jar5ActorFactory.restoreFromLocalState(actionPerformerLocalState)).thenReturn(
        actionPerformer);
    when(jar3ActorFactory.restoreFromLocalState(actionReceiverLocalState)).thenReturn(
        actionReceiver);

    when(actionPerformerLocalStateExtractor.extract(actionPerformer)).thenReturn(
        newActionPerformerLocalState);
    when(actionReceiverLocalStateExtractor.extract(actionReceiver)).thenReturn(
        newActionReceiverLocalState);
    when(transitiveActionTaker.take(TransitiveAction.builder()
            .actionTemplate(actionTemplate)
            .actionReceiver(actionReceiver)
            .actionPerformer(actionPerformer)
        .build()))
        .thenReturn(ActionResult.success());

    TransitiveActionTemplateExecutor executor = TransitiveActionTemplateExecutor.builder()
        .config(ActorStateTransformerConfig.builder()
            .actorFactories(ImmutableMap.of(
                actionPerformerDefinition, jar5ActorFactory,
                actionReceiverDefinition, jar3ActorFactory
            ))
            .localStateExtractors(ImmutableMap.of(
                actionPerformerDefinition, actionPerformerLocalStateExtractor,
                actionReceiverDefinition, actionReceiverLocalStateExtractor
            ))
            .build())
        .transitiveActionTaker(transitiveActionTaker)
        .build();

    assertThat(executor.execute(
        actionPerformerLocalState,
        actionPerformerDefinition,
        actionReceiverLocalState,
        actionTemplate))
        .isEqualTo(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(
                actionPerformerDefinition, newActionPerformerLocalState,
                actionReceiverDefinition, newActionReceiverLocalState
            )).build());

    verify(jar5ActorFactory).restoreFromLocalState(actionPerformerLocalState);
    verify(jar3ActorFactory).restoreFromLocalState(actionReceiverLocalState);
    verify(transitiveActionTaker).take(TransitiveAction.builder()
        .actionPerformer(actionPerformer)
        .actionReceiver(actionReceiver)
        .actionTemplate(actionTemplate)
        .build());
    verify(actionPerformerLocalStateExtractor).extract(actionPerformer);
    verify(actionReceiverLocalStateExtractor).extract(actionReceiver);

    verifyNoMoreInteractions(
        jar3ActorFactory,
        jar5ActorFactory,
        actionPerformerLocalStateExtractor,
        actionReceiverLocalStateExtractor);
  }
}