package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class SimpleTransactionManagerClient implements TransactionManagerClient {

  private final Map<Integer, ResourceManagerState> resourceManagerStates;

  @Override
  public void prepare(ResourceManagerClient resourceManagerClient) {
    int resourceManagerId = resourceManagerClient.getId();
    validateResourceManager(resourceManagerId, STATES_ALLOWED_FOR_PREPARE);
    resourceManagerStates.put(resourceManagerId, PREPARED);
  }

  @Override
  public void abort(ResourceManagerClient resourceManagerClient) {
    int resourceManagerId = resourceManagerClient.getId();
    validateResourceManager(resourceManagerId, STATES_ALLOWED_FOR_ABORT);
    resourceManagerClient.abort();
    resourceManagerStates.put(resourceManagerId, ABORTED);
  }

  @Override
  public void commit(ResourceManagerClient resourceManagerClient) {
    int resourceManagerId = resourceManagerClient.getId();
    validateResourceManager(resourceManagerId, STATES_ALLOWED_FOR_COMMIT);
    resourceManagerClient.commit();
    resourceManagerStates.put(resourceManagerId, COMMITTED);
  }

  private void validateResourceManager(int id, Set<ResourceManagerState> allowedState) {
    ResourceManagerState state = resourceManagerStates.get(id);
    Preconditions.checkState(state != null,
        "Could not find state for ResourceManager Id({})", id);
    Preconditions.checkState(allowedState.contains(state),
        "Current state {} is not allowed for the action", state);
  }
}
