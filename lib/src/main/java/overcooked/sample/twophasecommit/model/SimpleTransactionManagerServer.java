package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;

import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An implementation of {@link TransactionManagerServer} that is purely for model tracking.
 * TODO: could this be an actual implementation, and if not, how close can it be?
 */
@RequiredArgsConstructor
@SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
    justification = "this is just an example, making it immutable is over engineering")
public class SimpleTransactionManagerServer implements TransactionManagerServer {
  // TODO: this is required by {@link TransactionManagerLocalStateExtractor} but it is not part of
  //  the {@link TransactionManagerServer} implementation. Very weird.
  @Getter
  private final Map<String, ResourceManagerState> resourceManagerStates;

  @Override
  public void abort(String resourceManagerId) {
    validateCurrentState(STATES_ALLOWED_FOR_ABORT, resourceManagerId);
    resourceManagerStates.put(resourceManagerId, ABORTED);
  }

  @Override
  public void abort(ResourceManagerClient resourceManagerClient) {
    String id = resourceManagerClient.getId();
    validateCurrentState(STATES_ALLOWED_FOR_ABORT, id);

    resourceManagerClient.abort();
    resourceManagerStates.put(id, ABORTED);
  }

  @Override
  public void commit(ResourceManagerClient resourceManager) {
    String id = resourceManager.getId();
    validateCurrentState(STATES_ALLOWED_FOR_COMMIT, id);

    resourceManager.commit();
    resourceManagerStates.put(id, COMMITTED);
  }

  @Override
  public void prepare(String resourceManagerId) {
    validateCurrentState(STATES_ALLOWED_FOR_PREPARE, resourceManagerId);
    resourceManagerStates.put(resourceManagerId, PREPARED);
  }

  private void validateCurrentState(Set<ResourceManagerState> validStates,
                                    String resourceManagerId) {
    ResourceManagerState currentState =
        Preconditions.checkNotNull(resourceManagerStates.get(resourceManagerId));
    Preconditions.checkState(validStates.contains(currentState),
        "Current state %s is not allowed for the action", currentState);
  }
}
