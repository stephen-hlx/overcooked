package overcooked.sample.twophasecommit.modelverifier;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.ResourceManagerStateDao;

@AllArgsConstructor
class InMemoryResourceManagerClient implements ResourceManagerClient {
  @Getter
  private final String id;
  private final ResourceManagerStateDao stateDao;

  @Override
  public void commit() {
    ResourceManagerState resourceManagerState = this.stateDao.get();
    Preconditions.checkState(STATES_ALLOWED_FOR_COMMIT.contains(resourceManagerState),
        "Action commit is not allowed for current state {}", resourceManagerState);
    this.stateDao.save(COMMITTED);
  }

  @Override
  public void abort() {
    ResourceManagerState resourceManagerState = this.stateDao.get();
    Preconditions.checkState(STATES_ALLOWED_FOR_ABORT.contains(resourceManagerState),
        "Action abort is not allowed for current state {}", resourceManagerState);
    this.stateDao.save(ABORTED);
  }
}
