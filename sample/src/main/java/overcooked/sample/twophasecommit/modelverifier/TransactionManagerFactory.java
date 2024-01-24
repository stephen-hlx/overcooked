package overcooked.sample.twophasecommit.modelverifier;

import java.util.Map;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.LocalState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

class TransactionManagerFactory implements ActorFactory<TransactionManagerActor> {
  @Override
  public TransactionManagerActor restoreFromLocalState(LocalState localState) {
    TransactionManagerLocalState state = (TransactionManagerLocalState) localState;
    RefCell<Map<String, ResourceManagerState>> refCellState =
        new RefCell<>(state.getResourceManagerStates());
    return new TransactionManagerActor(refCellState);
  }
}
