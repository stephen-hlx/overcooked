package overcooked.sample.twophasecommit.modelverifier;

import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import overcooked.core.actor.ActorState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

@Value
@EqualsAndHashCode(callSuper = false)
class TransactionManagerActorState extends ActorState {
  @Getter
  Map<String, ResourceManagerState> resourceManagerStates;

  @Override
  public String toString() {
    return resourceManagerStates.toString();
  }
}
