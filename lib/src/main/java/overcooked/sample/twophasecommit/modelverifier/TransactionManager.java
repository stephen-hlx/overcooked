package overcooked.sample.twophasecommit.modelverifier;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;
import overcooked.sample.twophasecommit.model.TransactionManagerServer;

/**
 * The actor that represents both the {@link TransactionManagerClient} and
 * {@link TransactionManagerServer} for model checking.
 */
@Builder
public class TransactionManager implements TransactionManagerClient, TransactionManagerServer {
  @Getter(AccessLevel.PACKAGE)
  private final TransactionManagerServer transactionManagerServer;

  @Override
  public void prepare(String resourceManagerId) {
    transactionManagerServer.prepare(resourceManagerId);
  }

  @Override
  public void abort(String resourceManagerId) {
    transactionManagerServer.abort(resourceManagerId);
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
