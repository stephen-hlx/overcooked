package overcooked.analysis;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import overcooked.core.GlobalState;

/**
 * A class that captures the data of a state machine execution,
 * e.g. transitions, initial state.
 */
public class StateMachineExecutionDataCollector {
  private final Set<Transition> transitions = new HashSet<>();
  private final Set<GlobalState> validationFailingGlobalStates = new HashSet<>();

  @Getter
  private final GlobalState initialState;

  public StateMachineExecutionDataCollector(GlobalState globalState) {
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
