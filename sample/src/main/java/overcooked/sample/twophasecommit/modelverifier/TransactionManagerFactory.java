package overcooked.sample.twophasecommit.modelverifier;

import java.util.Map;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.LocalState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

class TransactionManagerFactory implements ActorFactory<TransactionManager> {
  @Override
  public TransactionManager restoreFromLocalState(LocalState localState) {
    TransactionManagerLocalState state = (TransactionManagerLocalState) localState;
    RefCell<Map<String, ResourceManagerState>> refCellState =
        new RefCell<>(state.getResourceManagerStates());
    return new TransactionManager(refCellState);
  }
}
