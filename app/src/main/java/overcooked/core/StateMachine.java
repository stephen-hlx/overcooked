package overcooked.core;


import lombok.Builder;
import overcooked.core.analysis.Analyser;

import java.util.*;

@Builder
public class StateMachine {
    private final StateMachineAdvancer stateMachineAdvancer;
    private final GlobalStateVerifier globalStateVerifier;

    public void run(GlobalState initialState,
                    ActorActionConfig actorActionConfig,
                    Analyser analyser) {
        Set<GlobalState> visited = new HashSet<>();
        doRun(initialState, actorActionConfig, visited, analyser);
    }

    private void doRun(GlobalState initialState,
                       ActorActionConfig actorActionConfig,
                       Set<GlobalState> visited,
                       Analyser analyser) {
        Queue<GlobalState> queue = new ArrayDeque<>();
        queue.add(initialState);
        while (!queue.isEmpty()) {
            GlobalState current = queue.remove();
            if (visited.contains(current)) {
                continue;
            }
            if (!globalStateVerifier.validate(current)) {
                analyser.addValidationFailingNode(current);
                visited.add(current);
                continue;
            }
            queue.addAll(stateMachineAdvancer.computeNext(current, actorActionConfig, analyser).stream()
                .filter(globalState -> !visited.contains(globalState))
                .toList());
            visited.add(current);
        }
    }

}
