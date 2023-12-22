package overcooked.sample.twophasecommit.modelverifier;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerServer;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;

/**
 * The actor that represents both {@link ResourceManagerClient} and {@link ResourceManagerServer}
 * for model checking.
 */
@SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
    justification = "this is just a sample")
public class ResourceManager implements ResourceManagerClient, ResourceManagerServer {
  @Getter
  private final String id;
  private final ResourceManagerServer resourceManagerServer;
  private final ResourceManagerClient resourceManagerClient;
  @Getter
  @SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
      justification = "this is for model verification only")
  private final RefCell<ResourceManagerState> state;

  /**
   * Constructor.
   *
   * @param id the ID of the {@link ResourceManager}
   * @param state the state of the {@link ResourceManager}
   */
  public ResourceManager(String id, RefCell<ResourceManagerState> state) {
    this.id = id;
    this.state = state;
    this.resourceManagerClient = new InMemoryResourceManagerClient(id, state);
    this.resourceManagerServer = new InMemoryResourceManagerServer(id, state);
  }

  @Override
  public void commit() {
    resourceManagerClient.commit();
  }

  @Override
  public void abort() {
    resourceManagerClient.abort();
  }

  @Override
  public void abort(TransactionManagerClient transactionManagerClient) {
    resourceManagerServer.abort(transactionManagerClient);
  }

  @Override
  public void prepare(TransactionManagerClient transactionManagerClient) {
    resourceManagerServer.prepare(transactionManagerClient);
  }
}
