package overcooked.sample.twophasecommit.modelverifier;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import overcooked.sample.twophasecommit.model.ResourceManager;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;

/**
 * The actor that represents both {@link ResourceManagerClient} and {@link ResourceManager}
 * for model checking.
 */
public class ResourceManagerActor implements ResourceManagerClient, ResourceManager {
  @Getter
  private final String id;
  private final ResourceManager resourceManager;
  private final ResourceManagerClient resourceManagerClient;
  @Getter
  @SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
      justification = "this is for model verification only")
  private final RefCell<ResourceManagerState> state;

  /**
   * Constructor.
   *
   * @param id the ID of the {@link ResourceManagerActor}
   * @param state the state of the {@link ResourceManagerActor}
   */
  public ResourceManagerActor(String id, RefCell<ResourceManagerState> state) {
    this.id = id;
    this.state = state;
    this.resourceManagerClient = new InMemoryResourceManagerClient(id, state);
    this.resourceManager = new InMemoryResourceManager(id, state);
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
    resourceManager.abort(transactionManagerClient);
  }

  @Override
  public void prepare(TransactionManagerClient transactionManagerClient) {
    resourceManager.prepare(transactionManagerClient);
  }
}
