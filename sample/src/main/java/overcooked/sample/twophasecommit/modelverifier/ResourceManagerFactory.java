package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

class ResourceManagerFactory implements ActorFactory<ResourceManagerActor> {
  @Override
  public ResourceManagerActor restoreFromActorState(ActorState actorState) {
    ResourceManagerActorState state = (ResourceManagerActorState) actorState;
    RefCell<ResourceManagerState> stateRefCell = new RefCell<>(state.getState());
    return new ResourceManagerActor(state.getId(), stateRefCell);
  }
}
