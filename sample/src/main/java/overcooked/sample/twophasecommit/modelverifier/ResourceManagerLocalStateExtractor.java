package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.sample.twophasecommit.model.SimpleResourceManagerServer;

class ResourceManagerLocalStateExtractor implements LocalStateExtractor<ResourceManager> {
  @Override
  public LocalState extract(ResourceManager resourceManager) {
    SimpleResourceManagerServer simpleResourceManagerServer =
        ((SimpleResourceManagerServer) resourceManager.getResourceManagerServer());
    return new ResourceManagerLocalState(
        simpleResourceManagerServer.getId(),
        simpleResourceManagerServer.getState());
  }
}
