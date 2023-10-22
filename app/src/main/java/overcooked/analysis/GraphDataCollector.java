package overcooked.analysis;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import overcooked.core.GlobalState;

/**
 * A graph data collector that  captures the data of a state machine,
 * e.g. transitions, initial state.
 */
public class GraphDataCollector {
  private final Set<Transition> transitions = new HashSet<>();
  private final Set<GlobalState> validationFailingGlobalStates = new HashSet<>();

  @Getter
  private final GlobalState initialState;

  public GraphDataCollector(GlobalState globalState) {
    initialState = globalState;
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
