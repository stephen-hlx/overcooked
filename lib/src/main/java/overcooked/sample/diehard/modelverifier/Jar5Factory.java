package overcooked.sample.diehard.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.LocalState;
import overcooked.sample.diehard.model.Jar5;

/**
 * A {@link ActorFactory} implementation for {@link Jar5}.
 */
class Jar5Factory implements ActorFactory<Jar5> {
  @Override
  public Jar5 restoreFromLocalState(LocalState localState) {
    Jar5State jar5State = (Jar5State) localState;
    return new Jar5(jar5State.getOccupancy());
  }
}
