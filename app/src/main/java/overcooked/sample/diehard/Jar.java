package overcooked.sample.diehard;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class Jar {
    private final int capacity;
    private int occupancy;

    public void addTo(Jar other) {
        Preconditions.checkArgument(this != other, "Cannot add to self");

        int volumeToMove = Math.min(this.occupancy, other.availableSpace());

        this.occupancy -= volumeToMove;
        other.occupancy = Math.min(other.capacity, other.occupancy + volumeToMove);
    }

    public void empty() {
        this.occupancy = 0;
    }

    public void fill() {
        this.occupancy = this.capacity;
    }


    public int availableSpace() {
        return this.capacity - this.occupancy;
    }
}
