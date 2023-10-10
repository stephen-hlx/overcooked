package overcooked.core;


import lombok.Builder;

import java.util.*;

@Builder
public class StateMachine {
    private final StateMachineAdvancer stateMachineAdvancer;
    private final GlobalStateVerifier globalStateVerifier;

    public void run(GlobalState initialState, ActorActionConfig actorActionConfig) {
        Set<GlobalState> visited = new HashSet<>();
        doRun(initialState, actorActionConfig, visited);
    }

    private void doRun(GlobalState initialState,
                       ActorActionConfig actorActionConfig,
                       Set<GlobalState> visited) {
        Queue<GlobalState> queue = new ArrayDeque<>();
        queue.add(initialState);
        while (!queue.isEmpty()) {
            GlobalState current = queue.remove();
            if (visited.contains(current)) {
                continue;
            }
            globalStateVerifier.verify(current);
            queue.addAll(stateMachineAdvancer.computeNext(current, actorActionConfig).stream()
                .filter(globalState -> !visited.contains(globalState))
                .toList());
            visited.add(current);
        }
    }

}
