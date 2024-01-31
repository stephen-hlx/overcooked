package overcooked.sample.waterjar.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.sample.waterjar.model.Jar3;

/**
 * A {@link LocalStateExtractor} implementation for {@link Jar3}.
 */
public class Jar3LocalStateExtractor implements LocalStateExtractor<Jar3> {
  @Override
  public LocalState extract(Jar3 jar3) {
    return new Jar3State(jar3.getOccupancy());
  }
}
