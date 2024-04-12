package overcooked.sample.waterjar.modelverifier;

import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateExtractor;
import overcooked.sample.waterjar.model.Jar5;

/**
 * A {@link ActorStateExtractor} implementation for {@link Jar5}.
 */
public class Jar5ActorStateExtractor implements ActorStateExtractor<Jar5> {
  @Override
  public ActorState extract(Jar5 jar5) {
    return new Jar5State(jar5.getOccupancy());
  }
}
