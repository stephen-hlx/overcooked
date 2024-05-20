package overcooked.sample.waterjar.model;

import com.google.common.base.Preconditions;
import overcooked.core.actor.ActorBase;

/**
 * A Jar with a capacity of 5.
 * This is made along with {@link Jar3} to test that the system supports actors of different types.
 */
public class Jar5 extends Jar implements ActorBase {
  private static final int CAPACITY = 5;

  public Jar5(int occupancy) {
    super(CAPACITY, occupancy);
    Preconditions.checkArgument(occupancy <= CAPACITY);
  }

  public void addTo(Jar3 other) {
    super.addTo(other);
  }
}
