package overcooked.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import overcooked.analysis.Arc;
import overcooked.analysis.StateMachineExecutionData;
import overcooked.analysis.Transition;

/**
 * A class that captures the context of a state machine execution,
 * e.g. transitions, initial state.
 */
public class StateMachineExecutionContext {
  private final Set<Transition> transitions = new HashSet<>();
  private final Set<GlobalState> validationFailingGlobalStates = new HashSet<>();
  private final GlobalState initialState;
  private final Map<Integer, GlobalState> globalStates = new HashMap<>();

  public StateMachineExecutionContext(GlobalState globalState) {
    initialState = globalState;
    registerOrGetDuplicate(globalState);
  }

  GlobalState registerOrGetDuplicate(GlobalState globalState) {
    return globalStates.computeIfAbsent(globalState.hashCode(), notUsed -> globalState);
  }

  void capture(GlobalState from, Arc arc, GlobalState to) {
    transitions.add(Transition.builder()
        .from(from)
        .arc(arc)
        .to(to)
        .build());
  }

  public void addValidationFailingNode(GlobalState globalState) {
    validationFailingGlobalStates.add(globalState);
  }

  /**
   * Returns the {@link StateMachineExecutionData} collected.
   *
   * @return the {@link StateMachineExecutionData} collected
   */
  public StateMachineExecutionData getData() {
    return StateMachineExecutionData.builder()
        .initialState(initialState)
        .transitions(transitions)
        .validationFailingGlobalStates(validationFailingGlobalStates)
        .build();
  }
}
