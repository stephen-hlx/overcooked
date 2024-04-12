package overcooked.sample.waterjar.modelverifier;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import overcooked.core.actor.ActorState;
import overcooked.sample.waterjar.model.Jar3;

/**
 * A {@link ActorState} implementation of {@link Jar3}.
 */
@RequiredArgsConstructor
@Value
@EqualsAndHashCode(callSuper = false)
public class Jar3State extends ActorState {
  int occupancy;

  public String toString() {
    return "occupancy=" + occupancy;
  }
}
