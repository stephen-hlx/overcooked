package overcooked.core.action;

import lombok.NonNull;
import lombok.Value;
import overcooked.core.actor.Actor;

/**
 * An {@link ActionType} that is transitive.
 */
@Value
public class TransitiveActionType implements ActionType {
  @NonNull
  Actor actionReceiverDefinition;

  @Override
  public boolean isTransitive() {
    return true;
  }
}
