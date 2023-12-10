package overcooked.sample.twophasecommit.modelverifier;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import overcooked.analysis.ExecutionSummary;
import overcooked.analysis.Report;

@Slf4j
class ModelVerifierTest {
  @Test
  void can_run_without_error() {
    Report report = new ModelVerifier().run();
    log.info(report.toString());
    assertThat(report.getExecutionSummary()).isEqualTo(ExecutionSummary.builder()
            .numOfValidationFailingStates(0)
            .numOfNonSelfTransitions(120)
            .numOfStates(34)
            .numOfTransitions(408)
        .build());
  }
}