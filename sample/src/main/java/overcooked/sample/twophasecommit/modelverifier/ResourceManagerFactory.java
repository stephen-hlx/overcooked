package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.LocalState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

class ResourceManagerFactory implements ActorFactory<ResourceManager> {
  @Override
  public ResourceManager restoreFromLocalState(LocalState localState) {
    ResourceManagerLocalState state = (ResourceManagerLocalState) localState;
    RefCell<ResourceManagerState> stateRefCell = new RefCell<>(state.getState());
    return new ResourceManager(state.getId(), stateRefCell);
  }
}
