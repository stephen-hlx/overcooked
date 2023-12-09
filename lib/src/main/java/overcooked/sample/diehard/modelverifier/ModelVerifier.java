package overcooked.sample.diehard.modelverifier;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import overcooked.analysis.JgraphtAnalyser;
import overcooked.analysis.ReportGenerator;
import overcooked.core.ActorActionConfig;
import overcooked.core.GlobalState;
import overcooked.core.StateMachine;
import overcooked.core.StateMachineExecutionContext;
import overcooked.core.StateMachineFactory;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.IntransitiveActionType;
import overcooked.core.action.ParamTemplate;
import overcooked.core.action.TransitiveActionType;
import overcooked.core.actor.Actor;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.sample.diehard.model.Jar3;
import overcooked.sample.diehard.model.Jar5;
import overcooked.visual.DotGraphExporterFactory;

/**
 * The ModelVerifier of example diehard.
 */
@Slf4j
class ModelVerifier {
  private static final Actor JAR3 = Actor.builder()
      .id("jar3")
      .build();
  private static final Actor JAR5 = Actor.builder()
      .id("jar5")
      .build();

  void verify() {
    GlobalState initialState = new GlobalState(ImmutableMap.of(
        JAR3, new Jar3State(0),
        JAR5, new Jar5State(0)));

    Set<ActionTemplate> jar3Templates = ImmutableSet.of(
        ActionTemplate.builder()
            .actionType(new IntransitiveActionType())
            .methodName("empty")
            .build(),
        ActionTemplate.builder()
            .actionType(new IntransitiveActionType())
            .methodName("fill")
            .build(),
        ActionTemplate.builder()
            .actionType(new TransitiveActionType(Actor.builder()
                .id("jar5")
                .build()))
            .methodName("addTo")
            .parameter(new ParamTemplate<>(Jar5.class))
            .build()
    );

    Set<ActionTemplate> jar5Templates = ImmutableSet.of(
        ActionTemplate.builder()
            .actionType(new IntransitiveActionType())
            .methodName("empty")
            .build(),
        ActionTemplate.builder()
            .actionType(new IntransitiveActionType())
            .methodName("fill")
            .build(),
        ActionTemplate.builder()
            .actionType(new TransitiveActionType(Actor.builder()
                .id("jar3")
                .build()))
            .methodName("addTo")
            .parameter(new ParamTemplate<>(Jar3.class))
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
    log.info(reportGenerator.generate(stateMachineExecutionContext.getData()).toString());
  }

  @SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED")
  private static void mkdir(String dirName) {
    log.info("Making dir " + dirName);
    new File(dirName).mkdirs();
  }
}
