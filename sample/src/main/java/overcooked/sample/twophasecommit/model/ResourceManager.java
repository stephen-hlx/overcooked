package overcooked.sample.twophasecommit.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A resource manager that can be in several states defined by {@link ResourceManagerState},
 * representing a real entity that can coordinate in a two phase commit scenario.
 */
@SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
    justification = "this is just a sample")
public interface ResourceManager {

  /**
   * Prepares for committing the transaction.
   */
  void prepare(TransactionManagerClient transactionManagerClient);

  /**
   * Aborts the transaction.
   */
  void abort(TransactionManagerClient transactionManagerClient);
}
