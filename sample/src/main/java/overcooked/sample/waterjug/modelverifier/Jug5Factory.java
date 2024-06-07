package overcooked.sample.waterjug.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorState;
import overcooked.sample.waterjug.model.Jug5;

/**
 * A {@link ActorFactory} implementation for {@link Jug5}.
 */
public class Jug5Factory implements ActorFactory<Jug5> {
  @Override
  public Jug5 restoreFromActorState(ActorState actorState) {
    Jug5State jug5State = (Jug5State) actorState;
    return new Jug5(jug5State.getOccupancy());
  }
}
