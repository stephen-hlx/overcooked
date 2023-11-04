package overcooked.sample.twophasecommit.model;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;

import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
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
  public void abort(ResourceManagerClient resourceManager) {
    String id = resourceManager.getId();
    ResourceManagerState currentState = Preconditions.checkNotNull(resourceManagerStates.get(id));
    Preconditions.checkState(STATES_ALLOWED_FOR_ABORT.contains(currentState),
        "Current state {} is not allowed for the action", currentState);

    resourceManager.abort();

    resourceManagerStates.put(id, ABORTED);
  }

  @Override
  public void commit(ResourceManagerClient resourceManager) {
    String id = resourceManager.getId();
    ResourceManagerState currentState = Preconditions.checkNotNull(resourceManagerStates.get(id));
    Preconditions.checkState(STATES_ALLOWED_FOR_COMMIT.contains(currentState),
        "Current state {} is not allowed for the action", currentState);

    resourceManager.commit();

    resourceManagerStates.put(id, COMMITTED);
  }
}
