package overcooked.analysis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableInt;
import overcooked.visual.DotGraphExporter;

/**
 * Generator of the report based on the execution data.
 */
@Builder
@Slf4j
public class ReportGenerator {
  private static final String FILENAME_ALL_TRANSITIONS = "all";
  private static final String FILENAME_FAILURE_TRANSITION = "failure";
  
  @Builder.Default
  private final String outputDirName = "/tmp/overcooked";

  private final DotGraphExporter dotGraphExporter;
  private final Analyser analyser;

  /**
   * Generate the report.
   *
   * @param data the {@link StateMachineExecutionData} based on which the report will be generated
   * @return a {@link Report} object
   */
  public Report generate(StateMachineExecutionData data) {
    exportGraphs(data);
    return Report.builder()
        .executionSummary(getSummary(data))
        .outputDirName(outputDirName)
        .build();
  }

  private void exportGraphs(StateMachineExecutionData data) {
    String allTransitions = String.format("%s/%s", outputDirName, FILENAME_ALL_TRANSITIONS);
    log.info("Exporting full state machine to " + allTransitions);
    writeToFile(allTransitions, dotGraphExporter.export(data.getTransitions()));

    MutableInt counter = new MutableInt(0);
    data.getValidationFailingGlobalStates().forEach(failingState -> {
      String filename = String.format("%s/%s_%d",
          outputDirName, FILENAME_FAILURE_TRANSITION, counter.getAndIncrement());
      log.info("Exporting failure state to " + filename);
      writeToFile(filename, dotGraphExporter.export(
          analyser.findShortestPathToFailureState(data.getInitialState(),
              failingState,
              data.getTransitions())));
    });
  }

  private void writeToFile(String filename, String data) {
    Path path = Paths.get(filename);
    try {
      Files.writeString(path, data, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static ExecutionSummary getSummary(StateMachineExecutionData data) {
    return ExecutionSummary.builder()
        .numOfTransitions(data.getTransitions().size())
        .numOfNonSelfTransitions(data.getTransitions().stream()
            .filter(transition -> !transition.getFrom().equals(transition.getTo()))
            .count())
        .numOfStates(data.getTransitions().stream()
            .flatMap(transition -> Stream.of(transition.getFrom(), transition.getTo()))
            .distinct()
            .count())
        .numOfValidationFailingStates(data.getValidationFailingGlobalStates().size())
        .build();
  }
}
