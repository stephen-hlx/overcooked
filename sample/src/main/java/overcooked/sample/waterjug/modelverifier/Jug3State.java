package overcooked.sample.waterjug.modelverifier;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import overcooked.core.actor.ActorState;
import overcooked.sample.waterjug.model.Jug3;

/**
 * A {@link ActorState} implementation of {@link Jug3}.
 */
@RequiredArgsConstructor
@Value
@EqualsAndHashCode(callSuper = false)
public class Jug3State extends ActorState {
  int occupancy;

  public String toString() {
    return "occupancy=" + occupancy;
  }
}
