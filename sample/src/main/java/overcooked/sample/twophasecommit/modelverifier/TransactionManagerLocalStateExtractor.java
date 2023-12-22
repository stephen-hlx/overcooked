package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;

class TransactionManagerLocalStateExtractor implements LocalStateExtractor<TransactionManager> {
  @Override
  public LocalState extract(TransactionManager transactionManager) {
    return new TransactionManagerLocalState(transactionManager.getStates().getData());
  }
}
