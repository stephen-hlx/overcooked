package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;

class ResourceManagerActorLocalStateExtractor implements LocalStateExtractor<ResourceManagerActor> {
  @Override
  public LocalState extract(ResourceManagerActor resourceManagerActor) {
    return new ResourceManagerLocalState(resourceManagerActor.getId(),
        resourceManagerActor.getState().getData());
  }
}
