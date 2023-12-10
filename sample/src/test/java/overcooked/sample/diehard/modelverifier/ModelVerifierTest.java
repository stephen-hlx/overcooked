package overcooked.sample.diehard.modelverifier;

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
        .numOfValidationFailingStates(2)
        .numOfNonSelfTransitions(50)
        .numOfStates(16)
        .numOfTransitions(84)
        .build());
  }
}