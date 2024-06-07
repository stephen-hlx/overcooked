package overcooked.sample.waterjug.modelverifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import overcooked.core.actor.ActorState;
import overcooked.sample.waterjug.model.Jug5;

/**
 * A {@link ActorState} implementation of {@link Jug5}.
 */
@RequiredArgsConstructor
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Jug5State extends ActorState {
  int occupancy;

  public String toString() {
    return "occupancy=" + occupancy;
  }
}
