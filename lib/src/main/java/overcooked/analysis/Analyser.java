package overcooked.analysis;

import java.util.Set;
import overcooked.core.GlobalState;

/**
 * The analyser of the execution of a state machine.
 */
public interface Analyser {
  /**
   * Finds the shortest path (in the form of a set of {@link Transition}s) from the initial state to
   * the failure state.
   *
   * @param initialState the initial state
   * @param failureState the failure state
   * @param transitions the transitions of the execution of the state machine
   * @return a set of {@link Transition}s representing the shortest path starting from the initial
   *     state
   */
  Set<Transition> findShortestPathToFailureState(GlobalState initialState,
                                                 GlobalState failureState,
                                                 Set<Transition> transitions);
}
