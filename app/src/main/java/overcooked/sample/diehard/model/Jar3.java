package overcooked.sample.diehard.model;

import com.google.common.base.Preconditions;

public class Jar3 extends Jar {
    private static final int CAPACITY = 3;
    public Jar3(int occupancy) {
        super(CAPACITY, occupancy);
        Preconditions.checkArgument(occupancy <= CAPACITY);
    }

    public Jar3() {
        super(CAPACITY);
    }

    public void addTo(Jar5 other) {
        super.addTo(other);
    }
}
