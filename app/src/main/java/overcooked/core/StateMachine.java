package overcooked.core;


import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import lombok.Builder;
import overcooked.analysis.GraphDataCollector;

/**
 * The state machine of the model verification.
 */
@Builder
public class StateMachine {
  private final StateMachineDriver stateMachineDriver;
  private final GlobalStateVerifier globalStateVerifier;

  public void run(GlobalState initialState,
                  ActorActionConfig actorActionConfig,
                  GraphDataCollector graphDataCollector) {
    Set<GlobalState> visited = new HashSet<>();
    doRun(initialState, actorActionConfig, visited, graphDataCollector);
  }

  private void doRun(GlobalState initialState,
                     ActorActionConfig actorActionConfig,
                     Set<GlobalState> visited,
                     GraphDataCollector graphDataCollector) {
    Queue<GlobalState> queue = new ArrayDeque<>();
    queue.add(initialState);
    while (!queue.isEmpty()) {
      GlobalState current = queue.remove();
      if (visited.contains(current)) {
        continue;
      }
      if (!globalStateVerifier.validate(current)) {
        graphDataCollector.addValidationFailingNode(current);
        visited.add(current);
        continue;
      }
      queue.addAll(
          stateMachineDriver.computeNext(current, actorActionConfig, graphDataCollector).stream()
              .filter(globalState -> !visited.contains(globalState))
              .toList());
      visited.add(current);
    }
  }

}
