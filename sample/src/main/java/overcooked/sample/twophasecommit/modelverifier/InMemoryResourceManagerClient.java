package overcooked.sample.twophasecommit.modelverifier;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

@AllArgsConstructor
class InMemoryResourceManagerClient implements ResourceManagerClient {
  @Getter
  private final String id;
  private final RefCell<ResourceManagerState> state;

  @Override
  public void commit() {
    ResourceManagerState resourceManagerState = this.state.getData();
    Preconditions.checkState(STATES_ALLOWED_FOR_COMMIT.contains(resourceManagerState),
        "Action commit is not allowed for current state {}", resourceManagerState);
    this.state.setData(COMMITTED);
  }

  @Override
  public void abort() {
    ResourceManagerState resourceManagerState = this.state.getData();
    Preconditions.checkState(STATES_ALLOWED_FOR_ABORT.contains(resourceManagerState),
        "Action abort is not allowed for current state {}", resourceManagerState);
    this.state.setData(ABORTED);
  }
}
