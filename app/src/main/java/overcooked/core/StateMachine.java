package overcooked.core;


import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import lombok.Builder;
import overcooked.analysis.GraphBuilder;

/**
 * The state machine of the model verification.
 */
@Builder
public class StateMachine {
  private final StateMachineAdvancer stateMachineAdvancer;
  private final GlobalStateVerifier globalStateVerifier;

  public void run(GlobalState initialState,
                  ActorActionConfig actorActionConfig,
                  GraphBuilder graphBuilder) {
    Set<GlobalState> visited = new HashSet<>();
    doRun(initialState, actorActionConfig, visited, graphBuilder);
  }

  private void doRun(GlobalState initialState,
                     ActorActionConfig actorActionConfig,
                     Set<GlobalState> visited,
                     GraphBuilder graphBuilder) {
    Queue<GlobalState> queue = new ArrayDeque<>();
    queue.add(initialState);
    while (!queue.isEmpty()) {
      GlobalState current = queue.remove();
      if (visited.contains(current)) {
        continue;
      }
      if (!globalStateVerifier.validate(current)) {
        graphBuilder.addValidationFailingNode(current);
        visited.add(current);
        continue;
      }
      queue.addAll(
          stateMachineAdvancer.computeNext(current, actorActionConfig, graphBuilder).stream()
              .filter(globalState -> !visited.contains(globalState))
              .toList());
      visited.add(current);
    }
  }

}
