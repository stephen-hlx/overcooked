package overcooked.sample.waterjug.modelverifier;

import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateExtractor;
import overcooked.sample.waterjug.model.Jug5;

/**
 * A {@link ActorStateExtractor} implementation for {@link Jug5}.
 */
public class Jug5ActorStateExtractor implements ActorStateExtractor<Jug5> {
  @Override
  public ActorState extract(Jug5 jug5) {
    return new Jug5State(jug5.getOccupancy());
  }
}
