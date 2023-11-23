package overcooked.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import overcooked.analysis.Arc;
import overcooked.analysis.StateMachineExecutionDataCollector;
import overcooked.analysis.Transition;
import overcooked.core.action.IntransitiveActionTemplateExecutor;
import overcooked.core.action.TransitiveActionTemplateExecutor;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;


/**
 * The driver of a state machine.
 * This object drives the state machine forward by computing the next state based on the current
 * state and the provided action.
 */
@Builder
class StateMachineDriver {
  private final IntransitiveActionTemplateExecutor intransitiveActionTemplateExecutor;
  private final TransitiveActionTemplateExecutor transitiveActionTemplateExecutor;
  private final StateMerger stateMerger;

  /**
   * Computes the next state.
   *
   * @param globalState        the current state of the state machine
   * @param actorActionConfig  the actor and action configuration from which the driver can discover
   *                           all actors and their actions
   * @param stateMachineExecutionDataCollector the object that collects the data of the state
   *                                           machine execution
   * @return a set of {@link GlobalState} that is the result of the actions performed by the actors
   */
  public Set<GlobalState> computeNext(
      GlobalState globalState,
      ActorActionConfig actorActionConfig,
      StateMachineExecutionDataCollector stateMachineExecutionDataCollector) {
    Set<GlobalState> nextStates = new HashSet<>();

    globalState.getActorDefinitions().forEach(actorDefinition ->
        actorActionConfig.getActionDefinitionTemplates()
            .getOrDefault(actorDefinition, Collections.emptySet())
            .forEach(actionTemplate -> {
              Transition.TransitionBuilder transitionBuilder = Transition.builder()
                  .from(globalState);
              Arc.ArcBuilder arcBuilder = Arc.builder()
                  .actionPerformerId(actorDefinition.getId())
                  .methodName(actionTemplate.getMethodName());
              Map<ActorDefinition, LocalState> newLocalStates;
              if (actionTemplate.getActionType().isTransitive()) {
                ActorDefinition actionReceiverDefinition =
                    actionTemplate.getActionType().getActionReceiverDefinition();
                arcBuilder.actionReceiverId(actionReceiverDefinition.getId());
                newLocalStates = transitiveActionTemplateExecutor.execute(
                    globalState.getCopyOfLocalState(actorDefinition),
                    actorDefinition,
                    globalState.getCopyOfLocalState(actionReceiverDefinition),
                    actionTemplate);
              } else {
                newLocalStates = intransitiveActionTemplateExecutor.execute(
                    globalState.getCopyOfLocalState(actorDefinition),
                    actorDefinition,
                    actionTemplate);
              }
              GlobalState newGlobalState = stateMerger.merge(globalState, newLocalStates);
              stateMachineExecutionDataCollector.capture(transitionBuilder
                  .arc(arcBuilder.build())
                  .to(newGlobalState)
                  .build());
              nextStates.add(newGlobalState);
            }));

    return nextStates;
  }
}
