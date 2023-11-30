package overcooked.core;

import java.util.HashSet;
import java.util.Set;
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

  public StateMachineExecutionContext(GlobalState globalState) {
    initialState = globalState;
  }

  public void capture(Transition transition) {
    this.transitions.add(transition);
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
