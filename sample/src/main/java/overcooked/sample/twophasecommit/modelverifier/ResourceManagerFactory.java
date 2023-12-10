package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.LocalState;
import overcooked.sample.twophasecommit.model.SimpleResourceManagerServer;

class ResourceManagerFactory implements ActorFactory<ResourceManager> {
  @Override
  public ResourceManager restoreFromLocalState(LocalState localState) {
    ResourceManagerLocalState state = (ResourceManagerLocalState) localState;
    return ResourceManager.builder()
        .resourceManagerServer(new SimpleResourceManagerServer(state.getId(), state.getState()))
        .build();
  }
}
