package overcooked.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static overcooked.analysis.StateMachineTestSetup.GLOBAL_STATE_0;
import static overcooked.analysis.StateMachineTestSetup.GLOBAL_STATE_4;
import static overcooked.analysis.StateMachineTestSetup.TRANSITION_0_0;
import static overcooked.analysis.StateMachineTestSetup.TRANSITION_0_1;
import static overcooked.analysis.StateMachineTestSetup.TRANSITION_0_2;
import static overcooked.analysis.StateMachineTestSetup.TRANSITION_0_3;
import static overcooked.analysis.StateMachineTestSetup.TRANSITION_2_3;
import static overcooked.analysis.StateMachineTestSetup.TRANSITION_3_4;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

class JgraphtAnalyserTest {
  private final JgraphtAnalyser analyser = new JgraphtAnalyser();

  @Test
  void works() {
    assertThat(analyser.findShortestPathToFailureState(GLOBAL_STATE_0,
        GLOBAL_STATE_4,
        ImmutableSet.of(
            TRANSITION_0_0,
            TRANSITION_0_1,
            TRANSITION_0_2,
            TRANSITION_0_3,
            TRANSITION_2_3,
            TRANSITION_3_4
        )))
        .isEqualTo(ImmutableList.of(
            TRANSITION_0_3,
            TRANSITION_3_4
        ));
  }

}