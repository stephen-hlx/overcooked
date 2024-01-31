package overcooked.sample.waterjar.modelverifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import overcooked.core.actor.LocalState;
import overcooked.sample.waterjar.model.Jar5;

/**
 * A {@link LocalState} implementation of {@link Jar5}.
 */
@RequiredArgsConstructor
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Jar5State extends LocalState {
  int occupancy;

  public String toString() {
    return "occupancy=" + occupancy;
  }
}
