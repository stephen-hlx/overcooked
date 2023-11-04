package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import com.google.common.collect.ImmutableSet;

/**
 * The interface of the transaction manager client.
 * It is supposed that there are 2 implementation of this interface:
 * - the real implementation used in production that deals with making the network call
 *   to the server of the transaction manager
 * - the in memory implementation that is used for model checking
 */
public interface TransactionManagerClient {
  ImmutableSet<ResourceManagerState> STATES_ALLOWED_FOR_PREPARE =
      ImmutableSet.of(WORKING, PREPARED);
  ImmutableSet<ResourceManagerState> STATES_ALLOWED_FOR_ABORT =
      ImmutableSet.of(PREPARED, ABORTED);

  void prepare(String resourceManagerId);

  void abort(String resourceManagerId);
}
