package overcooked.sample.diehard.model;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
class Jar {
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

  private int availableSpace() {
    return this.capacity - this.occupancy;
  }
}