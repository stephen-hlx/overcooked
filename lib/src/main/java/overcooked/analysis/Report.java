package overcooked.analysis;

import lombok.Builder;
import lombok.Value;

/**
 * The report of the state machine execution.
 */
@Builder
@Value
public class Report {
  ExecutionSummary executionSummary;
  String outputDirName;
}
