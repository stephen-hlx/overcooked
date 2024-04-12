package overcooked.sample.waterjar.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorState;
import overcooked.sample.waterjar.model.Jar5;

/**
 * A {@link ActorFactory} implementation for {@link Jar5}.
 */
public class Jar5Factory implements ActorFactory<Jar5> {
  @Override
  public Jar5 restoreFromActorState(ActorState actorState) {
    Jar5State jar5State = (Jar5State) actorState;
    return new Jar5(jar5State.getOccupancy());
  }
}
