package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.LocalState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

class ResourceManagerFactory implements ActorFactory<ResourceManagerActor> {
  @Override
  public ResourceManagerActor restoreFromLocalState(LocalState localState) {
    ResourceManagerLocalState state = (ResourceManagerLocalState) localState;
    RefCell<ResourceManagerState> stateRefCell = new RefCell<>(state.getState());
    return new ResourceManagerActor(state.getId(), stateRefCell);
  }
}
