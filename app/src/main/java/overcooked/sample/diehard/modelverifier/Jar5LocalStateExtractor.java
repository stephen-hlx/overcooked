package overcooked.sample.diehard.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.sample.diehard.model.Jar3;
import overcooked.sample.diehard.model.Jar5;

/**
 * A {@link LocalStateExtractor} implementation for {@link Jar5}.
 */
public class Jar5LocalStateExtractor implements LocalStateExtractor {
  @Override
  public LocalState extract(Object actor) {
    Jar5 jar5 = (Jar5) actor;
    return new Jar5State(jar5.getOccupancy());
  }
}
