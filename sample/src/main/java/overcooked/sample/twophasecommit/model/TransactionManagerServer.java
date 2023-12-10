package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import com.google.common.collect.ImmutableSet;

/**
 * The interface of a transaction manager server.
 * It is supposed that there are two implementation of this implementation:
 * - the real implementation used in production
 * - an in-memory implementation used in the model checking
 */
public interface TransactionManagerServer extends TransactionManagerClient {
  ImmutableSet<ResourceManagerState> STATES_ALLOWED_FOR_ABORT =
      ImmutableSet.of(WORKING, PREPARED, ABORTED);
  ImmutableSet<ResourceManagerState> STATES_ALLOWED_FOR_COMMIT =
      ImmutableSet.of(PREPARED, COMMITTED);

  void abort(ResourceManagerClient resourceManagerClient);

  void commit(ResourceManagerClient resourceManagerClient);
}
