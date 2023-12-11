package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.Actor;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.util.TestLocalState;

class TransitiveActionTemplateExecutorTest {
  private final TransitiveActionTaker transitiveActionTaker = mock(TransitiveActionTaker.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<Object> actionPerformerFactory = mock(ActorFactory.class);
  @SuppressWarnings("unchecked")
  private final ActorFactory<Object> actionReceiverFactory = mock(ActorFactory.class);
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
    String actionPerformer = "actionPerformerObject";
    LocalState actionPerformerLocalState = new TestLocalState(0, 0);
    LocalState newActionPerformerLocalState = new TestLocalState(0, 1);
    Actor actionPerformerDefinition = Actor.builder()
        .id("actionPerformer")
        .build();

    String actionReceiver = "actionReceiverObject";
    LocalState actionReceiverLocalState = new TestLocalState(1, 0);
    LocalState newActionReceiverLocalState = new TestLocalState(1, 1);
    Actor actionReceiverDefinition = Actor.builder()
        .id("actionReceiver")
        .build();

    ActionTemplate<String, String> actionTemplate = ActionTemplate.<String, String>builder()
        .actionType(new TransitiveActionType(actionReceiverDefinition))
        .build();

    when(actionPerformerFactory.restoreFromLocalState(actionPerformerLocalState)).thenReturn(
        actionPerformer);
    when(actionReceiverFactory.restoreFromLocalState(actionReceiverLocalState)).thenReturn(
        actionReceiver);

    when(actionPerformerLocalStateExtractor.extract(actionPerformer)).thenReturn(
        newActionPerformerLocalState);
    when(actionReceiverLocalStateExtractor.extract(actionReceiver)).thenReturn(
        newActionReceiverLocalState);
    when(transitiveActionTaker.take(TransitiveAction.<String, String>builder()
            .actionTemplate(actionTemplate)
            .actionReceiver(actionReceiver)
            .actionPerformer(actionPerformer)
        .build()))
        .thenReturn(ActionResult.success());

    TransitiveActionTemplateExecutor executor = TransitiveActionTemplateExecutor.builder()
        .config(ActorStateTransformerConfig.builder()
            .actorFactories(ImmutableMap.of(
                actionPerformerDefinition, actionPerformerFactory,
                actionReceiverDefinition, actionReceiverFactory
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

    verify(actionPerformerFactory).restoreFromLocalState(actionPerformerLocalState);
    verify(actionReceiverFactory).restoreFromLocalState(actionReceiverLocalState);
    verify(transitiveActionTaker).take(TransitiveAction.<String, String>builder()
        .actionPerformer(actionPerformer)
        .actionReceiver(actionReceiver)
        .actionTemplate(actionTemplate)
        .build());
    verify(actionPerformerLocalStateExtractor).extract(actionPerformer);
    verify(actionReceiverLocalStateExtractor).extract(actionReceiver);

    verifyNoMoreInteractions(
        actionReceiverFactory,
        actionPerformerFactory,
        actionPerformerLocalStateExtractor,
        actionReceiverLocalStateExtractor);
  }
}