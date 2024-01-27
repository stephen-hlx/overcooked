package overcooked.sample.twophasecommit.modelverifier;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;

import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.TransactionManager;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;

/**
 * An implementation of {@link TransactionManager} that is purely for model tracking.
 * TODO: could this be an actual implementation, and if not, how close can it be?
 */
@RequiredArgsConstructor
@SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
    justification = "this is just an example, making it immutable is over engineering")
public class InMemoryTransactionManagerClient implements TransactionManagerClient {
  private final Map<String, ResourceManagerState> resourceManagerStates;

  @Override
  public void prepare(String resourceManagerId) {
    if (resourceManagerStates.entrySet().stream()
        .filter(entry -> !entry.getKey().equals(resourceManagerId))
        .anyMatch(entry -> entry.getValue().equals(COMMITTED))
        && !resourceManagerStates.get(resourceManagerId).equals(PREPARED)) {
      throw new IllegalStateException(
          "Commit has started, action PREPARE is no allowed at this stage");
    }
    validateCurrentState(STATES_ALLOWED_FOR_PREPARE, resourceManagerId);
    resourceManagerStates.put(resourceManagerId, PREPARED);
  }

  @Override
  public void abort(String resourceManagerId) {
    if (resourceManagerStates.containsValue(COMMITTED)) {
      throw new IllegalStateException("Abort is not allowed when commit has started");
    }
    validateCurrentState(STATES_ALLOWED_FOR_SELF_ABORT, resourceManagerId);
    resourceManagerStates.put(resourceManagerId, ABORTED);
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
