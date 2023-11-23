package overcooked.sample.twophasecommit.modelverifier;

import lombok.EqualsAndHashCode;
import lombok.Value;
import overcooked.core.actor.LocalState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

@Value
@EqualsAndHashCode(callSuper = false)
class ResourceManagerLocalState extends LocalState {
  String id;
  ResourceManagerState state;
}
