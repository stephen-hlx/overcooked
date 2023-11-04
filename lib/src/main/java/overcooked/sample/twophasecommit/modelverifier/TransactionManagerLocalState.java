package overcooked.sample.twophasecommit.modelverifier;

import java.util.Map;
import lombok.Getter;
import lombok.Value;
import overcooked.core.actor.LocalState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

@Value
class TransactionManagerLocalState implements LocalState {
  @Getter
  Map<String, ResourceManagerState> resourceManagerStates;
}
