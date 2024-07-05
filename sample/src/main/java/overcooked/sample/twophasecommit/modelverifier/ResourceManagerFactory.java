package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorState;
import overcooked.sample.twophasecommit.model.ResourceManagerStateDao;

class ResourceManagerFactory implements ActorFactory<ResourceManagerActor> {
  @Override
  public ResourceManagerActor restoreFromActorState(ActorState actorState) {
    ResourceManagerActorState state = (ResourceManagerActorState) actorState;
    ResourceManagerStateDao stateDao =
        new InMemoryResourceManagerStateDao(((ResourceManagerActorState) actorState).getState());
    return new ResourceManagerActor(state.getId(), stateDao);
  }
}
