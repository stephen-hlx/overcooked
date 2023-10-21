package overcooked.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class GraphNode<IdT, ActionT> {
  @EqualsAndHashCode.Include
  private final IdT id;

  private final Map<ActionT, Set<GraphNode<IdT, ActionT>>> outgoingArcs = new HashMap<>();

  private final Set<GraphNode<IdT, ActionT>> parents = new HashSet<>();

  public void addArc(ActionT action, GraphNode<IdT, ActionT> graphNode) {
    outgoingArcs.computeIfAbsent(action, notUsed -> new HashSet<>())
        .add(graphNode);
  }

  public void addReverseArc(GraphNode<IdT, ActionT> graphNode) {
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
