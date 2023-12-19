package overcooked.core.action;

import lombok.NonNull;
import lombok.Value;
import overcooked.core.actor.ActorId;

/**
 * An {@link ActionType} that is transitive.
 */
@Value
public class TransitiveActionType implements ActionType {
  @NonNull
  ActorId actionReceiverId;

  @Override
  public boolean isTransitive() {
    return true;
  }
}
