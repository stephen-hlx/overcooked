package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.sample.twophasecommit.model.SimpleResourceManagerServer;

class ResourceManagerLocalStateExtractor implements LocalStateExtractor {
  @Override
  public LocalState extract(Object actor) {
    ResourceManager resourceManager = (ResourceManager) actor;
    SimpleResourceManagerServer simpleResourceManagerServer =
        ((SimpleResourceManagerServer) resourceManager.getResourceManagerServer());
    return new ResourceManagerLocalState(
        simpleResourceManagerServer.getId(),
        simpleResourceManagerServer.getState());
  }
}
