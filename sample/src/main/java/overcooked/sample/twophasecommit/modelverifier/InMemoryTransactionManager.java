package overcooked.sample.twophasecommit.modelverifier;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;

import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.TransactionManager;

/**
 * An implementation of {@link TransactionManager} that is purely for model tracking.
 * TODO: could this be an actual implementation, and if not, how close can it be?
 */
@RequiredArgsConstructor
@SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
    justification = "this is just an example, making it immutable is over engineering")
public class InMemoryTransactionManager implements TransactionManager {
  private final RefCell<Map<String, ResourceManagerState>> resourceManagerStates;

  @Override
  public void abort(ResourceManagerClient resourceManagerClient) {
    Map<String, ResourceManagerState> states = resourceManagerStates.getData();
    states.keySet()
        .forEach(key -> validateCurrentState(STATES_ALLOWED_FOR_ABORT, key));

    resourceManagerClient.abort();
    states.put(resourceManagerClient.getId(), ABORTED);
  }

  @Override
  public void commit(ResourceManagerClient resourceManager) {
    Map<String, ResourceManagerState> states = resourceManagerStates.getData();
    states.keySet()
        .forEach(key -> validateCurrentState(STATES_ALLOWED_FOR_COMMIT, key));

    resourceManager.commit();
    states.put(resourceManager.getId(), COMMITTED);
  }

  private void validateCurrentState(Set<ResourceManagerState> validStates,
                                    String resourceManagerId) {
    ResourceManagerState currentState =
        Preconditions.checkNotNull(resourceManagerStates.getData().get(resourceManagerId));
    Preconditions.checkState(validStates.contains(currentState),
        "Current state %s of ResourceManager(%s) is not allowed for the action",
        currentState, resourceManagerId);
  }
}
