package overcooked.sample.twophasecommit.modelverifier;

import lombok.Value;
import overcooked.core.actor.LocalState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

@Value
class ResourceManagerLocalState implements LocalState {
  String id;
  ResourceManagerState state;
}
