package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateExtractor;

class ResourceManagerActorStateExtractor implements ActorStateExtractor<ResourceManagerActor> {
  @Override
  public ActorState extract(ResourceManagerActor resourceManagerActor) {
    return new ResourceManagerActorState(resourceManagerActor.getId(),
        resourceManagerActor.getState().getData());
  }
}
