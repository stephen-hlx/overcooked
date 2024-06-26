package overcooked.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static overcooked.util.StateMachineTestSetup.GLOBAL_STATE_0;
import static overcooked.util.StateMachineTestSetup.GLOBAL_STATE_4;
import static overcooked.util.StateMachineTestSetup.TRANSITION_0_0;
import static overcooked.util.StateMachineTestSetup.TRANSITION_0_1;
import static overcooked.util.StateMachineTestSetup.TRANSITION_0_2;
import static overcooked.util.StateMachineTestSetup.TRANSITION_0_3;
import static overcooked.util.StateMachineTestSetup.TRANSITION_2_3_I;
import static overcooked.util.StateMachineTestSetup.TRANSITION_3_4;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import overcooked.util.StateMachineTestSetup;

class JgraphtAnalyserTest {
  private final JgraphtGraphBuilder graphBuilder = spy(new JgraphtGraphBuilder());
  private final JgraphtAnalyser analyser = new JgraphtAnalyser(graphBuilder);

  /**
   * See {@link StateMachineTestSetup} for the graph.
   */
  @Test
  void works() {
    ImmutableSet<Transition> transitions = ImmutableSet.of(
        TRANSITION_0_0,
        TRANSITION_0_1,
        TRANSITION_0_2,
        TRANSITION_0_3,
        TRANSITION_2_3_I,
        TRANSITION_3_4
    );
    assertThat(analyser.findShortestPathToFailureState(GLOBAL_STATE_0,
        GLOBAL_STATE_4,
        transitions))
        .isEqualTo(ImmutableSet.of(
            TRANSITION_0_3,
            TRANSITION_3_4
        ));
    verify(graphBuilder).build(transitions);
    verifyNoMoreInteractions(graphBuilder);
  }

}