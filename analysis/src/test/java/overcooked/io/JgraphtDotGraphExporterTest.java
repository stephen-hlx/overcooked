package overcooked.io;

import static org.assertj.core.api.Assertions.assertThat;
import static overcooked.util.StateMachineTestSetup.TRANSITION_0_1;
import static overcooked.util.StateMachineTestSetup.TRANSITION_2_3_I;
import static overcooked.util.StateMachineTestSetup.TRANSITION_2_3_II;
import static overcooked.util.StateMachineTestSetup.TRANSITION_3_4;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

class JgraphtDotGraphExporterTest {

  @Test
  @SuppressWarnings("linelength")
  void prints_dot_format_string() {
    assertThat(DotGraphExporterFactory.create()
        .export(ImmutableSet.of(
            TRANSITION_0_1,
            TRANSITION_2_3_I,
            TRANSITION_2_3_II,
            TRANSITION_3_4)))
        .isEqualTo("""
            digraph G {
              S_0 [ label="actor1(f1=1,f2=0), actor2(f1=2,f2=0), actor3(f1=3,f2=0), actor4(f1=4,f2=0)" ];
              S_1 [ label="actor1(f1=1,f2=1), actor2(f1=2,f2=0), actor3(f1=3,f2=0), actor4(f1=4,f2=0)" ];
              S_2 [ label="actor1(f1=1,f2=0), actor2(f1=2,f2=1), actor3(f1=3,f2=1), actor4(f1=4,f2=0)" ];
              S_3 [ label="actor1(f1=1,f2=0), actor2(f1=2,f2=1), actor3(f1=3,f2=0), actor4(f1=4,f2=0)" ];
              S_4 [ label="actor1(f1=1,f2=1), actor2(f1=2,f2=1), actor3(f1=3,f2=0), actor4(f1=4,f2=0)" ];
              S_0 -> S_1 [ label="actor1.actor1method1()" ];
              S_2 -> S_3 [ label="actor3.actor3method1()" ];
              S_2 -> S_3 [ label="actor3.actor3method2()" ];
              S_3 -> S_4 [ label="actor1.actor1method1()" ];
            }
            """);
  }
}