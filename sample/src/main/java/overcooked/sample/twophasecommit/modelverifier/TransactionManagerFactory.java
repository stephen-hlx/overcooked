package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.LocalState;

class TransactionManagerFactory implements ActorFactory<TransactionManagerActor> {
  @Override
  public TransactionManagerActor restoreFromLocalState(LocalState localState) {
    TransactionManagerLocalState state = (TransactionManagerLocalState) localState;
    return new TransactionManagerActor(state.getResourceManagerStates());
  }
}
