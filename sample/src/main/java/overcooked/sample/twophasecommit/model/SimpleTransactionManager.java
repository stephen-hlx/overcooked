package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;

import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * An implementation of {@link TransactionManager} that is purely for model tracking.
 */
@RequiredArgsConstructor
@SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
    justification = "this is just an example, making it immutable is over engineering")
public class SimpleTransactionManager implements TransactionManager {
  private final Map<String, ResourceManagerState> resourceManagerStates;

  @Override
  public void abort(ResourceManagerClient resourceManagerClient) {
    resourceManagerStates.keySet()
        .forEach(key -> validateCurrentState(STATES_ALLOWED_FOR_ABORT, key));

    resourceManagerClient.abort();
    resourceManagerStates.put(resourceManagerClient.getId(), ABORTED);
  }

  @Override
  public void commit(ResourceManagerClient resourceManager) {
    resourceManagerStates.keySet()
        .forEach(key -> validateCurrentState(STATES_ALLOWED_FOR_COMMIT, key));

    resourceManager.commit();
    resourceManagerStates.put(resourceManager.getId(), COMMITTED);
  }

  private void validateCurrentState(Set<ResourceManagerState> validStates,
                                    String resourceManagerId) {
    ResourceManagerState currentState =
        Preconditions.checkNotNull(resourceManagerStates.get(resourceManagerId));
    Preconditions.checkState(validStates.contains(currentState),
        "Current state %s of ResourceManager(%s) is not allowed for the action",
        currentState, resourceManagerId);
  }
}
