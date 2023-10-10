package overcooked.sample.diehard.model;

import com.google.common.base.Preconditions;

public class Jar5 extends Jar {
    private static final int CAPACITY = 5;
    public Jar5(int occupancy) {
        super(CAPACITY, occupancy);
        Preconditions.checkArgument(occupancy <= CAPACITY);
    }

    public Jar5() {
        super(CAPACITY);
    }

    public void addTo(Jar3 other) {
        super.addTo(other);
    }
}
