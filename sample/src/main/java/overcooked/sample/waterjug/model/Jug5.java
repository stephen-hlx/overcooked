package overcooked.sample.waterjug.model;

import com.google.common.base.Preconditions;
import overcooked.core.actor.ActorBase;

/**
 * A jug with a capacity of 5.
 * This is made along with {@link Jug3} to test that the system supports actors of different types.
 */
public class Jug5 extends Jug implements ActorBase {
  private static final int CAPACITY = 5;

  public Jug5(int occupancy) {
    super(CAPACITY, occupancy);
    Preconditions.checkArgument(occupancy <= CAPACITY);
  }

  public void addTo(Jug3 other) {
    super.addTo(other);
  }
}
