package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import com.google.common.collect.ImmutableSet;

/**
 * A resource manager that can be in several states defined by {@link ResourceManagerState},
 * representing a real entity that can coordinate in a two phase commit scenario.
 */
public interface ResourceManagerClient {
  ImmutableSet<ResourceManagerState> STATES_ALLOWED_FOR_PREPARE =
      ImmutableSet.of(WORKING, PREPARED);
  ImmutableSet<ResourceManagerState> STATES_ALLOWED_FOR_COMMIT =
      ImmutableSet.of(PREPARED, COMMITTED);
  ImmutableSet<ResourceManagerState> STATES_ALLOWED_FOR_ABORT =
      ImmutableSet.of(PREPARED, ABORTED);

  /**
   * Returns the ID of the resource manager.
   *
   * @return the ID of the resource manager
   */
  String getId();

  /**
   * Commits the transaction.
   */
  void commit();

  /**
   * Aborts the transaction.
   */
  void abort();
}
