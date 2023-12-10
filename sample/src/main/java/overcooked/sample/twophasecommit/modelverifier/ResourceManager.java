package overcooked.sample.twophasecommit.modelverifier;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Getter;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerServer;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;

/**
 * The actor that represents both {@link ResourceManagerClient} and {@link ResourceManagerServer}
 * for model checking.
 */
@Builder
@SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
    justification = "this is just a sample")
public class ResourceManager implements ResourceManagerClient, ResourceManagerServer {
  @Getter
  private final ResourceManagerServer resourceManagerServer;

  @Override
  public String getId() {
    return resourceManagerServer.getId();
  }

  @Override
  public void commit() {
    resourceManagerServer.commit();
  }

  @Override
  public void abort() {
    resourceManagerServer.abort();
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
