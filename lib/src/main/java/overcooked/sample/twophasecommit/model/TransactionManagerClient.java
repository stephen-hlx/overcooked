package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import com.google.common.collect.ImmutableSet;

interface TransactionManagerClient {
  ImmutableSet<ResourceManagerState> STATES_ALLOWED_FOR_PREPARE =
      ImmutableSet.of(WORKING, PREPARED);
  ImmutableSet<ResourceManagerState> STATES_ALLOWED_FOR_ABORT =
      ImmutableSet.of(PREPARED, ABORTED);
  ImmutableSet<ResourceManagerState> STATES_ALLOWED_FOR_COMMIT =
      ImmutableSet.of(PREPARED, COMMITTED);


  void prepare(ResourceManagerClient resourceManagerClient);

  void abort(ResourceManagerClient resourceManagerClient);

  void commit(ResourceManagerClient resourceManagerClient);
}
