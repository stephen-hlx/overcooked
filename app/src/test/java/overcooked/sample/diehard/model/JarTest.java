package overcooked.sample.diehard.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import overcooked.sample.diehard.model.Jar;

class JarTest {
    @Test
    void empty_empties_jar() {
        Jar jar = new Jar(5);
        assertThat(jar.getOccupancy()).isEqualTo(0);
        jar.fill();
        assertThat(jar.getOccupancy()).isEqualTo(5);
        jar.empty();
        assertThat(jar.getOccupancy()).isEqualTo(0);
    }

    @Test
    void fill_fills_up_jar() {
        Jar jar = new Jar(5);
        assertThat(jar.getOccupancy()).isEqualTo(0);
        jar.fill();
        assertThat(jar.getOccupancy()).isEqualTo(5);
    }

    static Object[][] add_to_test_cases() {
        return new Object[][]{
            // fromJar   toJar     beforeFromJar beforeToJar afterFromJar afterToJar
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
    void add_to_fills_target_jar_as_much_as_possible(
        int fromJarCapacity,
        int toJarCapacity,
        int beforeFromJarOccupancy,
        int beforeToJarOccupancy,
        int afterFromJarOccupancy,
        int afterToJarOccupancy) {
        Jar fromJar = new Jar(fromJarCapacity, beforeFromJarOccupancy);
        Jar toJar = new Jar(toJarCapacity, beforeToJarOccupancy);

        fromJar.addTo(toJar);
        assertThat(fromJar.getOccupancy()).isEqualTo(afterFromJarOccupancy);
        assertThat(toJar.getOccupancy()).isEqualTo(afterToJarOccupancy);
    }

    @Test
    void add_to_self_throws_IllegalArgumentException() {
        Jar jar = new Jar(0);
        assertThatThrownBy(() -> jar.addTo(jar))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cannot add to self");
    }
}
