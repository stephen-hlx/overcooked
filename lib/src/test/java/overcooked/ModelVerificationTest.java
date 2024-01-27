package overcooked;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import overcooked.analysis.ExecutionSummary;
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
import overcooked.sample.diehard.modelverifier.FourLiterVerifier;
import overcooked.sample.diehard.modelverifier.Jar3Factory;
import overcooked.sample.diehard.modelverifier.Jar3LocalStateExtractor;
import overcooked.sample.diehard.modelverifier.Jar3State;
import overcooked.sample.diehard.modelverifier.Jar5Factory;
import overcooked.sample.diehard.modelverifier.Jar5LocalStateExtractor;
import overcooked.sample.diehard.modelverifier.Jar5State;
import overcooked.visual.DotGraphExporterFactory;

@Slf4j
class ModelVerificationTest {
  private static final ActorId JAR3 = ActorId.builder()
      .id("jar3")
      .build();
  private static final ActorId JAR5 = ActorId.builder()
      .id("jar5")
      .build();

  @Test
  void sample_verification_works() {
    GlobalState initialState = new GlobalState(ImmutableMap.of(
        JAR3, new Jar3State(0),
        JAR5, new Jar5State(0)));

    ActorActionConfig actorActionConfig = createActorActionConfig();

    StateMachineExecutionContext
        stateMachineExecutionContext = new StateMachineExecutionContext(initialState);
    StateMachine stateMachine =
        StateMachineFactory.create(new FourLiterVerifier(), createActorStateTransformerConfig());

    stateMachine.run(initialState, actorActionConfig, stateMachineExecutionContext);

    Report report = ReportGenerator.builder()
        .analyser(new JgraphtAnalyser())
        .dotGraphExporter(DotGraphExporterFactory.create())
        .outputDirName("/tmp")
        .build()
        .generate(stateMachineExecutionContext.getData());

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
            JAR3, new Jar3Factory(),
            JAR5, new Jar5Factory()
        ))
        .localStateExtractors(ImmutableMap.of(
            JAR3, new Jar3LocalStateExtractor(),
            JAR5, new Jar5LocalStateExtractor()
        ))
        .build();
  }

  private static ActorActionConfig createActorActionConfig() {
    return new ActorActionConfig(ImmutableMap.of(
        JAR3, createJar3ActionTemplates(),
        JAR5, createJar5ActionTemplates()
    ));
  }

  private static Set<ActionTemplate<?, ?>> createJar3ActionTemplates() {
    return ImmutableSet.of(
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
  }

  private static Set<ActionTemplate<?, ?>> createJar5ActionTemplates() {
    return ImmutableSet.of(
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
  }
}