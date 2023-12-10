package overcooked.analysis;

import lombok.Builder;
import lombok.Value;

/**
 * An object that represents a summary of an execution.
 */
@Value
@Builder
public class ExecutionSummary {
  long numOfStates;
  long numOfValidationFailingStates;
  long numOfTransitions;
  long numOfNonSelfTransitions;

  @Override
  public String toString() {
    return """
        numOfStates: %s,
        numOfValidationFailingStates: %s,
        numOfTransitions: %s,
        numOfNonSelfTransitions: %s
        """.formatted(
        numOfStates,
        numOfValidationFailingStates,
        numOfTransitions,
        numOfNonSelfTransitions);
  }
}
