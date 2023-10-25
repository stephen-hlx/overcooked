package overcooked.sample.diehard.modelverifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import overcooked.core.actor.LocalState;

/**
 * A {@link LocalState} implementation of {@link overcooked.sample.diehard.model.Jar3}.
 */
@RequiredArgsConstructor
@Getter
@Value
public class Jar3State implements LocalState {
  int occupancy;

  public String toString() {
    return "occupancy=" + occupancy;
  }
}
