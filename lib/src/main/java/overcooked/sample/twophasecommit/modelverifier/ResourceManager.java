package overcooked.sample.twophasecommit.modelverifier;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Getter;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerServer;

/**
 * The actor that represents both {@link ResourceManagerClient} and {@link ResourceManagerServer}
 * for model checking.
 */
@Builder
@SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
    justification = "this is just a sample")
public class ResourceManager {
  private final ResourceManagerClient resourceManagerClient;
  @Getter
  private final ResourceManagerServer resourceManagerServer;

  public void commit() {
    resourceManagerClient.commit();
  }

  public void abort() {
    resourceManagerClient.abort();
  }

  public void abort(TransactionManager transactionManager) {
    transactionManager.abort(resourceManagerClient.getId());
  }

  public void prepare(TransactionManager transactionManager) {
    transactionManager.prepare(resourceManagerClient.getId());
  }
}
