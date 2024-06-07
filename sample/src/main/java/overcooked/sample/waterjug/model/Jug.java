package overcooked.sample.waterjug.model;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
@EqualsAndHashCode
class Jug {
  private final int capacity;
  private int occupancy;

  public void addTo(Jug other) {
    Preconditions.checkArgument(this != other, "Cannot add to self");

    int volumeToMove = Math.min(this.occupancy, other.availableSpace());

    other.setOccupancy(Math.min(other.capacity, other.getOccupancy() + volumeToMove));
    this.occupancy -= volumeToMove;
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
