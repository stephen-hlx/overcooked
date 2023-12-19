package overcooked.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import overcooked.core.action.ExecutionResult;
import overcooked.core.action.IntransitiveActionTemplateExecutor;
import overcooked.core.action.TransitiveActionTemplateExecutor;
import overcooked.core.actor.ActorId;


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

    from.getActorIds().forEach(actorId ->
        actorActionConfig.getActionTemplates()
            .getOrDefault(actorId, Collections.emptySet())
            .forEach(actionTemplate -> {
              ExecutionResult executionResult;

              if (actionTemplate.getActionType().isTransitive()) {
                ActorId actionReceiverId =
                    actionTemplate.getActionType().getActionReceiverId();
                executionResult = transitiveActionTemplateExecutor.execute(
                    actionTemplate,
                    from.getCopyOfLocalState(actorId),
                    from.getCopyOfLocalState(actionReceiverId));
              } else {
                executionResult = intransitiveActionTemplateExecutor.execute(
                    actionTemplate,
                    from.getCopyOfLocalState(actorId));
              }

              GlobalState to = stateMachineExecutionContext.registerOrGetDuplicate(
                  stateMerger.merge(from, executionResult.getLocalStates()));

              stateMachineExecutionContext.capture(
                  from, actionTemplate, to, executionResult.getActionResult());

              nextStates.add(to);
            }));

    return nextStates;
  }
}
