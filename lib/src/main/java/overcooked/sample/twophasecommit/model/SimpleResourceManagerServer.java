package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerClient.STATES_ALLOWED_FOR_ABORT;
import static overcooked.sample.twophasecommit.model.ResourceManagerClient.STATES_ALLOWED_FOR_COMMIT;
import static overcooked.sample.twophasecommit.model.ResourceManagerClient.STATES_ALLOWED_FOR_PREPARE;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A resource manager that can be in several states defined by {@link ResourceManagerState},
 * representing a real entity that can coordinate in a two phase commit scenario.
 */
@AllArgsConstructor
public class SimpleResourceManagerServer implements ResourceManagerServer {
  // TODO: this is required by {@link ResourceManagerLocalStateExtractor} but it is not part of the
  // {@link ResourceManagerServer} implementation. Very weird.
  @Getter
  private final String id;
  // TODO: this is required by {@link ResourceManagerLocalStateExtractor} but it is not part of the
  // {@link ResourceManagerServer} implementation. Very weird.
  @Getter
  private ResourceManagerState state;

  @Override
  public void prepare(TransactionManagerClient transactionManagerClient) {
    Preconditions.checkState(STATES_ALLOWED_FOR_PREPARE.contains(this.state),
        "Action prepare is not allowed for current state {}", this.state);
    transactionManagerClient.prepare(id);
    this.state = PREPARED;
  }

  @Override
  public void abort() {
    Preconditions.checkState(STATES_ALLOWED_FOR_ABORT.contains(this.state),
        "Action abort is not allowed for current state {}", this.state);
    this.state = ABORTED;
  }

  @Override
  public void abort(TransactionManagerClient transactionManagerClient) {
    Preconditions.checkState(STATES_ALLOWED_FOR_SELF_ABORT.contains(this.state),
        "Action abort is not allowed for current state {}", this.state);
    transactionManagerClient.abort(id);
    this.state = ABORTED;
  }

  @Override
  public void commit() {
    Preconditions.checkState(STATES_ALLOWED_FOR_COMMIT.contains(this.state),
        "Action commit is not allowed for current state {}", this.state);
    this.state = COMMITTED;
  }
}
