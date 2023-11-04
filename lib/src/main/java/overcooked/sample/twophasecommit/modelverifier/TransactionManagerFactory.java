package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.LocalState;
import overcooked.sample.twophasecommit.model.SimpleTransactionManagerServer;

class TransactionManagerFactory implements ActorFactory<TransactionManager> {
  @Override
  public TransactionManager restoreFromLocalState(LocalState localState) {
    TransactionManagerLocalState state = (TransactionManagerLocalState) localState;
    return TransactionManager.builder()
        .transactionManagerClient(
            new SimpleTransactionManagerClient(state.getResourceManagerStates()))
        .transactionManagerServer(
            new SimpleTransactionManagerServer(state.getResourceManagerStates()))
        .build();
  }
}
