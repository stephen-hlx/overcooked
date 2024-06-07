package overcooked.sample.waterjug.modelverifier;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import overcooked.analysis.ExecutionSummary;
import overcooked.analysis.JgraphtAnalyser;
import overcooked.analysis.Report;
import overcooked.analysis.ReportGenerator;
import overcooked.core.ActorActionConfig;
import overcooked.core.GlobalState;
import overcooked.core.ModelVerifier;
import overcooked.core.StateMachineExecutionContext;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.IntransitiveActionType;
import overcooked.core.action.TransitiveActionType;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;
import overcooked.io.DotGraphExporterFactory;
import overcooked.sample.waterjug.model.Jug3;
import overcooked.sample.waterjug.model.Jug5;

@Slf4j
class WaterJugModelVerifierTest {
  private static final ActorId JUG3 = new ActorId("jug3");
  private static final ActorId JUG5 = new ActorId("jug5");

  @Test
  void can_run_without_error() {
    ModelVerifier modelVerifier = ModelVerifier.builder()
        .actorActionConfig(createActorActionConfig())
        .actorStateTransformerConfig(createActorStateTransformerConfig())
        .invariantVerifier(new FourLiterVerifier())
        .build();

    ActorState actorState = new Jug5State(0);
    ActorState actorState1 = new Jug3State(0);
    StateMachineExecutionContext stateMachineExecutionContext =
        modelVerifier.runWith(new GlobalState(ImmutableMap.of(
            JUG3, LocalState.builder()
                .actorState(actorState1)
                .build(),
            JUG5, LocalState.builder()
                .actorState(actorState)
                .build())));

    String outputDirName = "/tmp/waterjug/" + System.currentTimeMillis();
    mkdir(outputDirName);
    ReportGenerator reportGenerator = ReportGenerator.builder()
        .analyser(new JgraphtAnalyser())
        .graphExporter(DotGraphExporterFactory.create())
        .outputDirName(outputDirName)
        .build();
    Report report = reportGenerator.generate(stateMachineExecutionContext.getData());
    log.info(report.toString());
    assertThat(report.getExecutionSummary()).isEqualTo(ExecutionSummary.builder()
        .numOfValidationFailingStates(2)
        .numOfNonSelfTransitions(50)
        .numOfStates(16)
        .numOfTransitions(84)
        .build());
  }

  private static ActorStateTransformerConfig createActorStateTransformerConfig() {
    return ActorStateTransformerConfig.builder()
        .actorFactories(ImmutableMap.of(
            JUG3, new Jug3Factory(),
            JUG5, new Jug5Factory()
        ))
        .actorStateExtractors(ImmutableMap.of(
            JUG3, new Jug3ActorStateExtractor(),
            JUG5, new Jug5ActorStateExtractor()
        ))
        .build();
  }

  private static ActorActionConfig createActorActionConfig() {
    return new ActorActionConfig(ImmutableMap.of(
        JUG3, createJug3ActionTemplates(),
        JUG5, createJug5ActionTemplates()
    ));
  }

  private static Set<ActionTemplate<?, ?>> createJug3ActionTemplates() {
    return ImmutableSet.of(
        ActionTemplate.<Jug3, Void>builder()
            .actionPerformerId(JUG3)
            .actionType(new IntransitiveActionType())
            .actionLabel("empty")
            .action(((jug3, unused) -> jug3.empty()))
            .build(),
        ActionTemplate.<Jug3, Void>builder()
            .actionPerformerId(JUG3)
            .actionType(new IntransitiveActionType())
            .actionLabel("fill")
            .action(((jug3, unused) -> jug3.fill()))
            .build(),
        ActionTemplate.<Jug3, Jug5>builder()
            .actionPerformerId(JUG3)
            .actionType(new TransitiveActionType(JUG5))
            .actionLabel("addTo")
            .action((Jug3::addTo))
            .build()
    );
  }

  private static Set<ActionTemplate<?, ?>> createJug5ActionTemplates() {
    return ImmutableSet.of(
        ActionTemplate.<Jug5, Void>builder()
            .actionPerformerId(JUG5)
            .actionType(new IntransitiveActionType())
            .actionLabel("empty")
            .action((jug5, unused) -> jug5.empty())
            .build(),
        ActionTemplate.<Jug5, Void>builder()
            .actionPerformerId(JUG5)
            .actionType(new IntransitiveActionType())
            .actionLabel("fill")
            .action((jug5, unused) -> jug5.fill())
            .build(),
        ActionTemplate.<Jug5, Jug3>builder()
            .actionPerformerId(JUG5)
            .actionType(new TransitiveActionType(JUG3))
            .actionLabel("addTo")
            .action(Jug5::addTo)
            .build()
    );
  }

  @SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED")
  private static void mkdir(String dirName) {
    log.info("Making dir " + dirName);
    new File(dirName).mkdirs();
  }
}