package overcooked.util;

import lombok.EqualsAndHashCode;
import lombok.Value;
import overcooked.core.actor.LocalState;

@Value
@EqualsAndHashCode(callSuper = false)
class TestLocalState extends LocalState {
  int f1;
  int f2;

  public String toString() {
    return String.format("f1=%d,f2=%d", f1, f2);
  }
}
