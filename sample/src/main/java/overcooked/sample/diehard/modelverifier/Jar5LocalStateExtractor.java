package overcooked.sample.diehard.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.sample.diehard.model.Jar5;

/**
 * A {@link LocalStateExtractor} implementation for {@link Jar5}.
 */
class Jar5LocalStateExtractor implements LocalStateExtractor<Jar5> {
  @Override
  public LocalState extract(Jar5 jar5) {
    return new Jar5State(jar5.getOccupancy());
  }
}
