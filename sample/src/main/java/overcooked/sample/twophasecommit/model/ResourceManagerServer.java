package overcooked.sample.twophasecommit.model;

/**
 * A resource manager that can be in several states defined by {@link ResourceManagerState},
 * representing a real entity that can coordinate in a two phase commit scenario.
 */
public interface ResourceManagerServer {

  /**
   * Prepares for committing the transaction.
   */
  void prepare(TransactionManagerClient transactionManagerClient);

  /**
   * Aborts the transaction.
   */
  void abort(TransactionManagerClient transactionManagerClient);
}
