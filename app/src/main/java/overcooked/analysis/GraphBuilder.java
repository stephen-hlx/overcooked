package overcooked.analysis;

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import overcooked.core.GlobalState;

/**
 * The builder of a graph.
 * It captures the transitions of a state machine.
 */
public class GraphBuilder {
  private final Set<Transition> transitions = new HashSet<>();
  private final Set<GlobalState> validationFailingGlobalStates = new HashSet<>();

  Set<GlobalStateNode> getNodes() {
    Map<GlobalState, GlobalStateNode> nodes = new HashMap<>();
    transitions.forEach(transition -> {
      GlobalStateNode from = getTo(nodes, transition.getFrom());
      GlobalStateNode to = getTo(nodes, transition.getTo());
      from.addArc(transition.getArc(), to);
      to.addReverseArc(from);
    });
    return ImmutableSet.copyOf(nodes.values());
  }

  private GlobalStateNode getTo(Map<GlobalState, GlobalStateNode> nodes, GlobalState transition) {
    return nodes.computeIfAbsent(transition,
        globalState -> new GlobalStateNode(isFailureNode(globalState), globalState));
  }

  private boolean isFailureNode(GlobalState fromGlobalState) {
    return validationFailingGlobalStates.contains(fromGlobalState);
  }

  public void capture(Transition transition) {
    this.transitions.add(transition);
  }

  public Set<Transition> getTransitions() {
    return ImmutableSet.copyOf(this.transitions);
  }

  public void addValidationFailingNode(GlobalState globalState) {
    validationFailingGlobalStates.add(globalState);
  }

  public Set<GlobalState> getValidationFailingGlobalStates() {
    return ImmutableSet.copyOf(validationFailingGlobalStates);
  }
}
