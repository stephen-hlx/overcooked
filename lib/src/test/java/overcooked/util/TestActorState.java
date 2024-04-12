package overcooked.util;

import lombok.EqualsAndHashCode;
import lombok.Value;
import overcooked.core.actor.ActorState;

/**
 * A {@link ActorState} implementation for use by tests.
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class TestActorState extends ActorState {
  int f1;
  int f2;

  public String toString() {
    return String.format("f1=%d,f2=%d", f1, f2);
  }
}
