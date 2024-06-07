package overcooked.sample.waterjug.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class JugTest {
  @Test
  void empty_empties_jug() {
    Jug jug = new Jug(5);
    assertThat(jug.getOccupancy()).isEqualTo(0);
    jug.fill();
    assertThat(jug.getOccupancy()).isEqualTo(5);
    jug.empty();
    assertThat(jug.getOccupancy()).isEqualTo(0);
  }

  @Test
  void fill_fills_up_jug() {
    Jug jug = new Jug(5);
    assertThat(jug.getOccupancy()).isEqualTo(0);
    jug.fill();
    assertThat(jug.getOccupancy()).isEqualTo(5);
  }

  static Object[][] add_to_test_cases() {
    return new Object[][] {
        // fromJug   toJug     beforeFromJug beforeToJug afterFromJug afterToJug
        // capacity, capacity, occupancy,    occupancy,  occupancy,   occupancy
        {  3,        5,        0,            0,          0,           0         },
        {  3,        5,        0,            1,          0,           1         },
        {  3,        5,        0,            5,          0,           5         },
        {  3,        5,        1,            1,          0,           2         },
        {  3,        5,        1,            4,          0,           5         },
        {  3,        5,        2,            4,          1,           5         },
        {  3,        5,        1,            5,          1,           5         },
    };
  }

  @ParameterizedTest
  @MethodSource("add_to_test_cases")
  void add_to_fills_target_jug_as_much_as_possible(
      int fromJugCapacity,
      int toJugCapacity,
      int beforeFromJugOccupancy,
      int beforeToJugOccupancy,
      int afterFromJugOccupancy,
      int afterToJugOccupancy) {
    Jug fromJug = new Jug(fromJugCapacity, beforeFromJugOccupancy);
    Jug toJug = new Jug(toJugCapacity, beforeToJugOccupancy);

    fromJug.addTo(toJug);
    assertThat(fromJug.getOccupancy()).isEqualTo(afterFromJugOccupancy);
    assertThat(toJug.getOccupancy()).isEqualTo(afterToJugOccupancy);
  }

  @Test
  void add_to_self_throws_IllegalArgumentException() {
    Jug jug = new Jug(0);
    assertThatThrownBy(() -> jug.addTo(jug))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cannot add to self");
  }
}
