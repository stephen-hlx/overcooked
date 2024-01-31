package overcooked.sample.waterjar.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.LocalState;
import overcooked.sample.waterjar.model.Jar3;

/**
 * A {@link ActorFactory} implementation for {@link Jar3}.
 */
public class Jar3Factory implements ActorFactory<Jar3> {
  @Override
  public Jar3 restoreFromLocalState(LocalState localState) {
    Jar3State jar3State = (Jar3State) localState;
    return new Jar3(jar3State.getOccupancy());
  }
}
