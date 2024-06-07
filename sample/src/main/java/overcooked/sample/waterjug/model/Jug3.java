package overcooked.sample.waterjug.model;

import com.google.common.base.Preconditions;
import overcooked.core.actor.ActorBase;

/**
 * A jug with a capacity of 3.
 * This is made along with {@link Jug5} to test that the system supports actors of different types.
 */
public class Jug3 extends Jug implements ActorBase {
  private static final int CAPACITY = 3;

  public Jug3(int occupancy) {
    super(CAPACITY, occupancy);
    Preconditions.checkArgument(occupancy <= CAPACITY);
  }

  public void addTo(Jug5 other) {
    super.addTo(other);
  }
}
