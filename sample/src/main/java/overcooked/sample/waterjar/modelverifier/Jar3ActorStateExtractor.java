package overcooked.sample.waterjar.modelverifier;

import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateExtractor;
import overcooked.sample.waterjar.model.Jar3;

/**
 * A {@link ActorStateExtractor} implementation for {@link Jar3}.
 */
public class Jar3ActorStateExtractor implements ActorStateExtractor<Jar3> {
  @Override
  public ActorState extract(Jar3 jar3) {
    return new Jar3State(jar3.getOccupancy());
  }
}
