package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerClient.validateCurrentStateIsAllowedToMoveToNewState;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;

/**
 * A resource manager that can be in several states defined by {@link ResourceManagerState},
 * representing a real entity that can coordinate in a two phase commit scenario.
 */
public class SimpleResourceManagerClient implements ResourceManagerClient {
  @Getter
  private final int id;
  private final TransactionManagerClient transactionManagerClient;
  @Getter
  private ResourceManagerState state;

  SimpleResourceManagerClient(int id,
                              TransactionManagerClient transactionManagerClient,
                              ResourceManagerState state) {
    this.id = id;
    this.transactionManagerClient = transactionManagerClient;
    this.state = state;
  }

  @Override
  public void prepare() {
    validateCurrentStateIsAllowedToMoveToNewState(this.state,
        PREPARED,
        ImmutableSet.of(WORKING, PREPARED));
    this.state = PREPARED;
    transactionManagerClient.prepare(this);
  }

  @Override
  public void commit() {
    validateCurrentStateIsAllowedToMoveToNewState(this.state,
        COMMITTED,
        ImmutableSet.of(PREPARED, COMMITTED));
    this.state = ResourceManagerState.COMMITTED;
  }

  @Override
  public void abort() {
    validateCurrentStateIsAllowedToMoveToNewState(this.state,
        ABORTED,
        ImmutableSet.of(PREPARED, ABORTED));
    this.state = ABORTED;
  }

  @Override
  public void selfAbort() {
    validateCurrentStateIsAllowedToMoveToNewState(this.state,
        ABORTED,
        ImmutableSet.of(PREPARED, ABORTED));
    this.state = ABORTED;
  }
}
