package overcooked.sample.twophasecommit.modelverifier;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;
import overcooked.sample.twophasecommit.model.TransactionManagerServer;

/**
 * The actor that represents both the {@link TransactionManagerClient} and
 * {@link TransactionManagerServer} for model checking.
 */
@Builder
public class TransactionManager {
  private final TransactionManagerClient transactionManagerClient;
  @Getter(AccessLevel.PACKAGE)
  private final TransactionManagerServer transactionManagerServer;

  public void prepare(String resourceManagerId) {
    transactionManagerClient.prepare(resourceManagerId);
  }

  public void abort(String resourceManagerId) {
    transactionManagerClient.abort(resourceManagerId);
  }

  public void abort(ResourceManager resourceManager) {
    resourceManager.abort();
  }

  public void commit(ResourceManager resourceManager) {
    resourceManager.commit();
  }
}
