package overcooked.analysis;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class GraphNode<Type, Action> {
    @EqualsAndHashCode.Include
    private final Type id;

    private final Map<Action, Set<GraphNode<Type, Action>>> outgoingArcs = new HashMap<>();

    private final Set<GraphNode<Type, Action>> parents = new HashSet<>();

    public void addArc(Action action, GraphNode<Type, Action> graphNode) {
        outgoingArcs.computeIfAbsent(action, notUsed -> new HashSet<>())
            .add(graphNode);
    }

    public void addReverseArc(GraphNode<Type, Action> graphNode) {
        parents.add(graphNode);
    }

    @Override
    public String toString() {
        return String.format("GraphNode(id=%s,outgoingArcs=(%s),parents=(%s))",
            id,
            printOutgoingArcs(),
            printParents());
    }

    private String printOutgoingArcs() {
        return outgoingArcs.entrySet().stream()
            .map(entry -> String.format("(%s->{%s})",
                entry.getKey(),
                entry.getValue().stream()
                    .map(GraphNode::getId)
                    .map(Objects::toString)
                    .collect(Collectors.joining(","))
            ))
            .collect(Collectors.joining(","));
    }

    private String printParents() {
        return parents.stream()
            .map(GraphNode::getId)
            .map(Objects::toString)
            .collect(Collectors.joining(","));
    }
}
