package overcooked.core;


import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import lombok.Builder;

/**
 * The state machine of the model verification.
 */
@Builder
public class StateMachine {
  private final StateMachineDriver stateMachineDriver;
  private final InvariantVerifier invariantVerifier;

  public void run(GlobalState initialState,
                  ActorActionConfig actorActionConfig,
                  StateMachineExecutionContext stateMachineExecutionContext) {
    Set<GlobalState> visited = new HashSet<>();
    doRun(initialState, actorActionConfig, visited, stateMachineExecutionContext);
  }

  private void doRun(GlobalState initialState,
                     ActorActionConfig actorActionConfig,
                     Set<GlobalState> visited,
                     StateMachineExecutionContext stateMachineExecutionContext) {
    Queue<GlobalState> queue = new ArrayDeque<>();
    queue.add(initialState);
    while (!queue.isEmpty()) {
      GlobalState current = queue.remove();
      if (visited.contains(current)) {
        continue;
      }
      if (!invariantVerifier.verify(current)) {
        stateMachineExecutionContext.addValidationFailingNode(current);
        visited.add(current);
        continue;
      }
      queue.addAll(
          stateMachineDriver.computeNext(current, actorActionConfig,
                  stateMachineExecutionContext).stream()
              .filter(globalState -> !visited.contains(globalState))
              .toList());
      visited.add(current);
    }
  }
}
