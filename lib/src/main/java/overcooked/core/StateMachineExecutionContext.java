package overcooked.core;

import java.util.HashSet;
import java.util.Set;
import overcooked.analysis.Arc;
import overcooked.analysis.StateMachineExecutionData;
import overcooked.analysis.Transition;
import overcooked.core.action.ActionResult;

/**
 * A class that captures the context of a state machine execution,
 * e.g. transitions, initial state.
 */
public class StateMachineExecutionContext {
  private final Set<Transition> transitions = new HashSet<>();
  private final Set<GlobalState> validationFailingGlobalStates = new HashSet<>();
  private final GlobalState initialState;
  private final Set<GlobalState> globalStates = new HashSet<>();

  public StateMachineExecutionContext(GlobalState globalState) {
    initialState = globalState;
    registerOrGetDuplicate(globalState);
  }

  GlobalState registerOrGetDuplicate(GlobalState globalState) {
    if (globalStates.contains(globalState)) {
      return globalStates.stream()
          .filter(globalState::equals)
          .findAny()
          .orElseThrow(() -> new RuntimeException("Should never happen "
              + "unless undetected concurrent modification happened"));
    }
    globalStates.add(globalState);
    return globalState;
  }

  void capture(GlobalState from, Arc arc, GlobalState to, ActionResult actionResult) {
    transitions.add(Transition.builder()
        .from(from)
        .arc(arc)
        .to(to)
        .actionResult(actionResult)
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
