package overcooked.sample.diehard.modelverifier;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import overcooked.analysis.JgraphtAnalyser;
import overcooked.analysis.Report;
import overcooked.analysis.ReportGenerator;
import overcooked.core.ActorActionConfig;
import overcooked.core.GlobalState;
import overcooked.core.StateMachine;
import overcooked.core.StateMachineExecutionContext;
import overcooked.core.StateMachineFactory;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.IntransitiveActionType;
import overcooked.core.action.TransitiveActionType;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.sample.diehard.model.Jar3;
import overcooked.sample.diehard.model.Jar5;
import overcooked.visual.DotGraphExporterFactory;

/**
 * The ModelVerifier of example diehard.
 */
@Slf4j
class ModelVerifier {
  private static final ActorId JAR3 = ActorId.builder()
      .id("jar3")
      .build();
  private static final ActorId JAR5 = ActorId.builder()
      .id("jar5")
      .build();

  Report run() {
    GlobalState initialState = new GlobalState(ImmutableMap.of(
        JAR3, new Jar3State(0),
        JAR5, new Jar5State(0)));

    Set<ActionTemplate<?, ?>> jar3Templates = ImmutableSet.of(
        ActionTemplate.<Jar3, Void>builder()
            .actionPerformerId(JAR3)
            .actionType(new IntransitiveActionType())
            .actionLabel("empty")
            .action(((jar3, unused) -> jar3.empty()))
            .build(),
        ActionTemplate.<Jar3, Void>builder()
            .actionPerformerId(JAR3)
            .actionType(new IntransitiveActionType())
            .actionLabel("fill")
            .action(((jar3, unused) -> jar3.fill()))
            .build(),
        ActionTemplate.<Jar3, Jar5>builder()
            .actionPerformerId(JAR3)
            .actionType(new TransitiveActionType(JAR5))
            .actionLabel("addTo")
            .action((Jar3::addTo))
            .build()
    );

    Set<ActionTemplate<?, ?>> jar5Templates = ImmutableSet.of(
        ActionTemplate.<Jar5, Void>builder()
            .actionPerformerId(JAR5)
            .actionType(new IntransitiveActionType())
            .actionLabel("empty")
            .action((jar5, unused) -> jar5.empty())
            .build(),
        ActionTemplate.<Jar5, Void>builder()
            .actionPerformerId(JAR5)
            .actionType(new IntransitiveActionType())
            .actionLabel("fill")
            .action((jar5, unused) -> jar5.fill())
            .build(),
        ActionTemplate.<Jar5, Jar3>builder()
            .actionPerformerId(JAR5)
            .actionType(new TransitiveActionType(JAR3))
            .actionLabel("addTo")
            .action(Jar5::addTo)
            .build()
    );

    ActorActionConfig actorActionConfig = new ActorActionConfig(ImmutableMap.of(
        JAR3, jar3Templates,
        JAR5, jar5Templates
    ));

    StateMachineExecutionContext
        stateMachineExecutionContext = new StateMachineExecutionContext(initialState);
    StateMachine stateMachine =
        StateMachineFactory.create(new FourLiterVerifier(), ActorStateTransformerConfig.builder()
            .actorFactories(ImmutableMap.of(
                JAR3, new Jar3Factory(),
                JAR5, new Jar5Factory()
            ))
            .localStateExtractors(ImmutableMap.of(
                JAR3, new Jar3LocalStateExtractor(),
                JAR5, new Jar5LocalStateExtractor()
            ))
            .build());

    stateMachine.run(initialState, actorActionConfig, stateMachineExecutionContext);

    String outputDirName = "/tmp/diehard/" + System.currentTimeMillis();
    mkdir(outputDirName);
    ReportGenerator reportGenerator = ReportGenerator.builder()
        .analyser(new JgraphtAnalyser())
        .dotGraphExporter(DotGraphExporterFactory.create())
        .outputDirName(outputDirName)
        .build();
    return reportGenerator.generate(stateMachineExecutionContext.getData());
  }

  @SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED")
  private static void mkdir(String dirName) {
    log.info("Making dir " + dirName);
    new File(dirName).mkdirs();
  }
}
