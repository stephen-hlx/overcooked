package overcooked.sample.waterjar.model;

import com.google.common.base.Preconditions;

/**
 * A Jar with a capacity of 3.
 * This is made along with {@link Jar5} to test that the system supports actors of different types.
 */
public class Jar3 extends Jar {
  private static final int CAPACITY = 3;

  public Jar3(int occupancy) {
    super(CAPACITY, occupancy);
    Preconditions.checkArgument(occupancy <= CAPACITY);
  }

  public void addTo(Jar5 other) {
    super.addTo(other);
  }
}
