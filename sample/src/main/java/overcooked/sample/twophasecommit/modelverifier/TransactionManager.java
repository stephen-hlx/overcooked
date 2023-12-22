package overcooked.sample.twophasecommit.modelverifier;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import lombok.Getter;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;
import overcooked.sample.twophasecommit.model.TransactionManagerServer;

/**
 * The actor that represents both the {@link TransactionManagerClient} and
 * {@link TransactionManagerServer} for model checking.
 */
public class TransactionManager implements TransactionManagerClient, TransactionManagerServer {
  @Getter
  @SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
      justification = "this is just an example, making it immutable is over engineering")
  private final RefCell<Map<String, ResourceManagerState>> states;
  private final TransactionManagerServer transactionManagerServer;
  private final TransactionManagerClient transactionManagerClient;

  /**
   * Constructor.
   *
   * @param states the {@link RefCell} object that containing the {@link ResourceManagerState}s
   */
  public TransactionManager(RefCell<Map<String, ResourceManagerState>> states) {
    this.states = states;
    this.transactionManagerClient = new InMemoryTransactionManagerClient(states);
    this.transactionManagerServer = new InMemoryTransactionManagerServer(states);
  }

  @Override
  public void prepare(String resourceManagerId) {
    transactionManagerClient.prepare(resourceManagerId);
  }

  @Override
  public void abort(String resourceManagerId) {
    transactionManagerClient.abort(resourceManagerId);
  }

  @Override
  public void abort(ResourceManagerClient resourceManagerClient) {
    transactionManagerServer.abort(resourceManagerClient);
  }

  @Override
  public void commit(ResourceManagerClient resourceManagerClient) {
    transactionManagerServer.commit(resourceManagerClient);
  }
}
