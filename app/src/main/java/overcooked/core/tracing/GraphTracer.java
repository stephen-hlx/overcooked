package overcooked.core.tracing;

import java.util.HashSet;
import java.util.Set;

public class GraphTracer {
    private final Set<GlobalStateNode> nodes;

    public GraphTracer() {
        nodes = new HashSet<>();
    }

    public void capture(Transition transition) {
    }
}
