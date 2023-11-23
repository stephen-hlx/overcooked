package overcooked.sample.twophasecommit.modelverifier;

import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import overcooked.core.actor.LocalState;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

@Value
@EqualsAndHashCode(callSuper = false)
class TransactionManagerLocalState extends LocalState {
  @Getter
  Map<String, ResourceManagerState> resourceManagerStates;
}
