package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.sample.twophasecommit.model.SimpleTransactionManagerServer;

class TransactionManagerLocalStateExtractor implements LocalStateExtractor {
  @Override
  public LocalState extract(Object actor) {
    TransactionManager transactionManager = (TransactionManager) actor;
    return new TransactionManagerLocalState(
        ((SimpleTransactionManagerServer) transactionManager.getTransactionManagerServer())
            .getResourceManagerStates());
  }
}
