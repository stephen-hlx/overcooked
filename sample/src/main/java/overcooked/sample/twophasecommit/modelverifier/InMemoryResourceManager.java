package overcooked.sample.twophasecommit.modelverifier;

import static overcooked.sample.twophasecommit.model.ResourceManagerClient.STATES_ALLOWED_FOR_PREPARE;
import static overcooked.sample.twophasecommit.model.ResourceManagerClient.STATES_ALLOWED_FOR_SELF_ABORT;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;

import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import overcooked.sample.twophasecommit.model.ResourceManager;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;

/**
 * A resource manager that can be in several states defined by {@link ResourceManagerState},
 * representing a real entity that can coordinate in a two phase commit scenario.
 */
@AllArgsConstructor
public class InMemoryResourceManager implements ResourceManager {
  private final String id;
  @SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
      justification = "this is for model verification only")
  private final RefCell<ResourceManagerState> state;

  @Override
  public void prepare(TransactionManagerClient transactionManagerClient) {
    ResourceManagerState resourceManagerState = this.state.getData();
    Preconditions.checkState(STATES_ALLOWED_FOR_PREPARE.contains(resourceManagerState),
        "Action prepare is not allowed for current state {}", resourceManagerState);
    transactionManagerClient.prepare(id);
    this.state.setData(PREPARED);
  }

  @Override
  public void selfAbort(TransactionManagerClient transactionManagerClient) {
    ResourceManagerState resourceManagerState = this.state.getData();
    Preconditions.checkState(STATES_ALLOWED_FOR_SELF_ABORT.contains(resourceManagerState),
        "Action selfAbort is not allowed for current state {}", resourceManagerState);
    transactionManagerClient.abort(id);
    this.state.setData(ABORTED);
  }
}
