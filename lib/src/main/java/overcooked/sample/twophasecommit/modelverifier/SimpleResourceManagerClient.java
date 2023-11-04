package overcooked.sample.twophasecommit.modelverifier;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

/**
 * A resource manager that can be in several states defined by {@link ResourceManagerState},
 * representing a real entity that can coordinate in a two phase commit scenario.
 */
public class SimpleResourceManagerClient implements ResourceManagerClient {
  @Getter
  private final String id;
  @Getter(AccessLevel.PACKAGE)
  private ResourceManagerState state;

  SimpleResourceManagerClient(String id, ResourceManagerState state) {
    this.id = id;
    this.state = state;
  }

  @Override
  public void commit() {
    Preconditions.checkState(STATES_ALLOWED_FOR_COMMIT.contains(this.state),
        "Action commit is not allowed for current state {}", this.state);
    this.state = ResourceManagerState.COMMITTED;
  }

  @Override
  public void abort() {
    Preconditions.checkState(STATES_ALLOWED_FOR_ABORT.contains(this.state),
        "Action abort is not allowed for current state {}", this.state);
    this.state = ABORTED;
  }

}
