package overcooked.sample.twophasecommit.modelverifier;

import lombok.Getter;
import overcooked.core.actor.ActorBase;
import overcooked.sample.twophasecommit.model.ResourceManager;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerStateDao;
import overcooked.sample.twophasecommit.model.SimpleResourceManager;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;

/**
 * The actor that represents both {@link ResourceManagerClient} and {@link ResourceManager}
 * for model checking.
 */
public class ResourceManagerActor
    implements ResourceManagerClient, ResourceManager, ActorBase {
  @Getter
  private final String id;
  private final ResourceManager resourceManager;
  private final ResourceManagerClient resourceManagerClient;
  @Getter
  private final ResourceManagerStateDao stateDao;

  /**
   * Constructor.
   *
   * @param id the ID of the {@link ResourceManagerActor}
   * @param stateDao the {@link ResourceManagerStateDao}
   */
  public ResourceManagerActor(String id, ResourceManagerStateDao stateDao) {
    this.id = id;
    this.stateDao = stateDao;
    this.resourceManagerClient = new InMemoryResourceManagerClient(id, stateDao);
    this.resourceManager = new SimpleResourceManager(id, stateDao);
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
  public void selfAbort(TransactionManagerClient transactionManagerClient) {
    resourceManager.selfAbort(transactionManagerClient);
  }

  @Override
  public void prepare(TransactionManagerClient transactionManagerClient) {
    resourceManager.prepare(transactionManagerClient);
  }
}
