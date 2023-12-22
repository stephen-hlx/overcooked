package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;

class ResourceManagerLocalStateExtractor implements LocalStateExtractor<ResourceManager> {
  @Override
  public LocalState extract(ResourceManager resourceManager) {
    return new ResourceManagerLocalState(resourceManager.getId(),
        resourceManager.getState().getData());
  }
}
