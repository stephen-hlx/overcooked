package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.sample.twophasecommit.model.SimpleTransactionManagerServer;

class TransactionManagerLocalStateExtractor implements LocalStateExtractor<TransactionManager> {
  @Override
  public LocalState extract(TransactionManager transactionManager) {
    return new TransactionManagerLocalState(
        ((SimpleTransactionManagerServer) transactionManager.getTransactionManagerServer())
            .getResourceManagerStates());
  }
}
