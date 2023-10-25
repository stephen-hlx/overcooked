package overcooked.visual;

import lombok.Value;
import overcooked.core.actor.LocalState;

@Value
class TestLocalState implements LocalState {
  int f1;
  int f2;

  public String toString() {
    return String.format("f1=%d,f2=%d", f1, f2);
  }
}
