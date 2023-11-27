package overcooked.core.action;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import overcooked.core.actor.Actor;

/**
 * An {@link ActionType} that is transitive.
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class TransitiveActionType implements ActionType {
  private final Actor actionReceiverDefinition;

  @Override
  public boolean isTransitive() {
    return true;
  }
}
