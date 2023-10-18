package overcooked.core;


import lombok.Builder;
import overcooked.core.tracing.Tracer;

import java.util.*;

@Builder
public class StateMachine {
    private final StateMachineAdvancer stateMachineAdvancer;
    private final GlobalStateVerifier globalStateVerifier;

    public void run(GlobalState initialState,
                    ActorActionConfig actorActionConfig,
                    Tracer tracer) {
        Set<GlobalState> visited = new HashSet<>();
        doRun(initialState, actorActionConfig, visited, tracer);
    }

    private void doRun(GlobalState initialState,
                       ActorActionConfig actorActionConfig,
                       Set<GlobalState> visited,
                       Tracer tracer) {
        Queue<GlobalState> queue = new ArrayDeque<>();
        queue.add(initialState);
        while (!queue.isEmpty()) {
            GlobalState current = queue.remove();
            if (visited.contains(current)) {
                continue;
            }
            globalStateVerifier.verify(current);
            queue.addAll(stateMachineAdvancer.computeNext(current, actorActionConfig, tracer).stream()
                .filter(globalState -> !visited.contains(globalState))
                .toList());
            visited.add(current);
        }
    }

}
