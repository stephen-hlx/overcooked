package overcooked.sample.twophasecommit.modelverifier;

import lombok.EqualsAndHashCode;
import lombok.Value;
import overcooked.core.actor.ActorState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

@Value
@EqualsAndHashCode(callSuper = false)
class ResourceManagerActorState extends ActorState {
  String id;
  ResourceManagerState state;

  @Override
  public String toString() {
    return state.toString();
  }
}
