package overcooked.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import overcooked.analysis.Arc;
import overcooked.core.action.ExecutionResult;
import overcooked.core.action.IntransitiveActionTemplateExecutor;
import overcooked.core.action.TransitiveActionTemplateExecutor;
import overcooked.core.actor.Actor;


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
   * @param from the current state of the state machine
   * @param actorActionConfig  the actor and action configuration from which the driver can discover
   *                           all actors and their actions
   * @param stateMachineExecutionContext the object that collects the data of the state
   *                                     machine execution
   * @return a set of {@link GlobalState} that is the result of the actions performed by the actors
   */
  Set<GlobalState> computeNext(
      GlobalState from,
      ActorActionConfig actorActionConfig,
      StateMachineExecutionContext stateMachineExecutionContext) {
    Set<GlobalState> nextStates = new HashSet<>();

    from.getActorDefinitions().forEach(actorDefinition ->
        actorActionConfig.getActionDefinitionTemplates()
            .getOrDefault(actorDefinition, Collections.emptySet())
            .forEach(actionTemplate -> {
              Arc.ArcBuilder arcBuilder = Arc.builder()
                  .actionPerformerId(actorDefinition.getId())
                  .label(actionTemplate.getActionLabel());
              ExecutionResult executionResult;

              if (actionTemplate.getActionType().isTransitive()) {
                Actor actionReceiverDefinition =
                    actionTemplate.getActionType().getActionReceiverDefinition();
                arcBuilder.actionReceiverId(actionReceiverDefinition.getId());
                executionResult = transitiveActionTemplateExecutor.execute(
                    actionTemplate,
                    from.getCopyOfLocalState(actorDefinition),
                    from.getCopyOfLocalState(actionReceiverDefinition));
              } else {
                executionResult = intransitiveActionTemplateExecutor.execute(
                    actionTemplate,
                    from.getCopyOfLocalState(actorDefinition));
              }

              GlobalState to = stateMachineExecutionContext.registerOrGetDuplicate(
                  stateMerger.merge(from, executionResult.getLocalStates()));

              stateMachineExecutionContext.capture(
                  from, arcBuilder.build(), to, executionResult.getActionResult());

              nextStates.add(to);
            }));

    return nextStates;
  }
}
