package overcooked.sample.twophasecommit.model;

import java.util.Set;

/**
 * A resource manager that can be in several states defined by {@link ResourceManagerState},
 * representing a real entity that can coordinate in a two phase commit scenario.
 */
interface ResourceManagerClient {
  /**
   * Returns the ID of the resource manager client.
   *
   * @return the ID of the resource manager client
   */
  int getId();

  /**
   * Prepares for committing the transaction.
   */
  void prepare();

  /**
   * Commits the transaction.
   */
  void commit();

  /**
   * Aborts the transaction.
   */
  void abort();

  /**
   * Aborts the transaction.
   */
  void selfAbort();

  static void validateCurrentStateIsAllowedToMoveToNewState(
      ResourceManagerState currentState,
      ResourceManagerState newState,
      Set<ResourceManagerState> allowedSet) {
    if (!allowedSet.contains(currentState)) {
      throw new IllegalStateException(
          String.format(
              "Moving to state %s is only possible from state(s) %s but it was %s",
              newState,
              allowedSet,
              currentState));
    }
  }
}
