package overcooked.visual;

import static org.assertj.core.api.Assertions.assertThat;
import static overcooked.analysis.StateMachineTestSetup.TRANSITION_0_2;
import static overcooked.analysis.StateMachineTestSetup.TRANSITION_2_3;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

class JgraphtDotGraphExporterTest {

  @Test
  @SuppressWarnings("linelength")
  void prints_dot_format_string() {
    assertThat(DotGraphExporterFactory.create()
        .export(ImmutableSet.of(
            TRANSITION_0_2,
            TRANSITION_2_3)))
        .isEqualTo("strict digraph G {\n"
            + "  S_0 [ label=\"actor1(f1=1,f2=0), actor2(f1=2,f2=0), actor3(f1=3,f2=0), actor4(f1=4,f2=0)\" ];\n"
            + "  S_2 [ label=\"actor1(f1=1,f2=0), actor2(f1=2,f2=1), actor3(f1=3,f2=1), actor4(f1=4,f2=0)\" ];\n"
            + "  S_3 [ label=\"actor1(f1=1,f2=0), actor2(f1=2,f2=1), actor3(f1=3,f2=0), actor4(f1=4,f2=0)\" ];\n"
            + "  S_0 -> S_2 [ label=\"actor2.actor2.method1(actor3)\" ];\n"
            + "  S_2 -> S_3 [ label=\"actor3.actor3.method1()\" ];\n"
            + "}\n");
  }

}