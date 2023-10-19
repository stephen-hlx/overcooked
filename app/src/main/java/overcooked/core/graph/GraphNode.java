package overcooked.core.graph;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class GraphNode<Type, Action> {
    @EqualsAndHashCode.Include
    private final Type id;

    private final Map<Action, Set<GraphNode<Type, Action>>> outgoingArcs = new HashMap<>();
    boolean failureNode = false;

    public void setFailureNode() {
        this.failureNode = true;
    }

    public void addArc(Action action, GraphNode<Type, Action> graphNode) {
        outgoingArcs.computeIfAbsent(action, notUsed -> new HashSet<>())
            .add(graphNode);
    }
}
