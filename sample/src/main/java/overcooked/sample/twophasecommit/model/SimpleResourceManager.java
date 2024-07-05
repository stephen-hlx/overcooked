package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerClient.STATES_ALLOWED_FOR_PREPARE;
import static overcooked.sample.twophasecommit.model.ResourceManagerClient.STATES_ALLOWED_FOR_SELF_ABORT;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;

/**
 * A resource manager that can be in several states defined by {@link ResourceManagerState},
 * representing a real entity that can coordinate in a two phase commit scenario.
 */
@AllArgsConstructor
public class SimpleResourceManager implements ResourceManager {
  private final String id;
  private final ResourceManagerStateDao stateDao;

  @Override
  public void prepare(TransactionManagerClient transactionManagerClient) {
    ResourceManagerState resourceManagerState = this.stateDao.get();
    Preconditions.checkState(STATES_ALLOWED_FOR_PREPARE.contains(resourceManagerState),
        "Action prepare is not allowed for current state {}", resourceManagerState);
    transactionManagerClient.prepare(id);
    this.stateDao.save(PREPARED);
  }

  @Override
  public void selfAbort(TransactionManagerClient transactionManagerClient) {
    ResourceManagerState resourceManagerState = this.stateDao.get();
    Preconditions.checkState(STATES_ALLOWED_FOR_SELF_ABORT.contains(resourceManagerState),
        "Action selfAbort is not allowed for current state {}", resourceManagerState);
    transactionManagerClient.abort(id);
    this.stateDao.save(ABORTED);
  }
}
