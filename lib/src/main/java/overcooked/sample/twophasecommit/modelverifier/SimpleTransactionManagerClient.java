package overcooked.sample.twophasecommit.modelverifier;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;

@Getter
@RequiredArgsConstructor
class SimpleTransactionManagerClient implements TransactionManagerClient {

  private final Map<String, ResourceManagerState> resourceManagerStates;

  @Override
  public void prepare(String resourceManagerId) {
    validateResourceManager(resourceManagerId, STATES_ALLOWED_FOR_PREPARE);
    resourceManagerStates.put(resourceManagerId, PREPARED);
  }

  @Override
  public void abort(String resourceManagerId) {
    validateResourceManager(resourceManagerId, STATES_ALLOWED_FOR_ABORT);
    resourceManagerStates.put(resourceManagerId, ABORTED);
  }

  private void validateResourceManager(String id, Set<ResourceManagerState> allowedState) {
    ResourceManagerState state = resourceManagerStates.get(id);
    Preconditions.checkState(state != null,
        "Could not find state for ResourceManager Id(%s)", id);
    Preconditions.checkState(allowedState.contains(state),
        "Current state %s is not allowed for the action", state);
  }
}
