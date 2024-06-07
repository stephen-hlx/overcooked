package overcooked.sample.waterjug.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorState;
import overcooked.sample.waterjug.model.Jug3;

/**
 * A {@link ActorFactory} implementation for {@link Jug3}.
 */
public class Jug3Factory implements ActorFactory<Jug3> {
  @Override
  public Jug3 restoreFromActorState(ActorState actorState) {
    Jug3State jug3State = (Jug3State) actorState;
    return new Jug3(jug3State.getOccupancy());
  }
}
