package overcooked.sample.waterjug.modelverifier;

import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateExtractor;
import overcooked.sample.waterjug.model.Jug3;

/**
 * A {@link ActorStateExtractor} implementation for {@link Jug3}.
 */
public class Jug3ActorStateExtractor implements ActorStateExtractor<Jug3> {
  @Override
  public ActorState extract(Jug3 jug3) {
    return new Jug3State(jug3.getOccupancy());
  }
}
