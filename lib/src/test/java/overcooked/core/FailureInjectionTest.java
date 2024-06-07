package overcooked.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import overcooked.analysis.ExecutionSummary;
import overcooked.analysis.JgraphtAnalyser;
import overcooked.analysis.Report;
import overcooked.analysis.ReportGenerator;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.IntransitiveActionType;
import overcooked.core.action.TransitiveActionType;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;
import overcooked.core.actor.SimulatedFailure;
import overcooked.io.DotGraphExporterFactory;
import overcooked.sample.waterjug.model.Jug3;
import overcooked.sample.waterjug.model.Jug5;
import overcooked.sample.waterjug.modelverifier.FourLiterVerifier;
import overcooked.sample.waterjug.modelverifier.Jug3ActorStateExtractor;
import overcooked.sample.waterjug.modelverifier.Jug3Factory;
import overcooked.sample.waterjug.modelverifier.Jug3State;
import overcooked.sample.waterjug.modelverifier.Jug5ActorStateExtractor;
import overcooked.sample.waterjug.modelverifier.Jug5Factory;
import overcooked.sample.waterjug.modelverifier.Jug5State;

@Slf4j
class FailureInjectionTest {
  private static final ActorId JUG3 = new ActorId("jug3");
  private static final ActorId JUG5 = new ActorId("jug5");

  @Test
  void sample_verification_works() {
    GlobalState initialState = new GlobalState(ImmutableMap.of(
        JUG3, LocalState.builder()
            .actorState(new Jug3State(0))
            .build(),
        JUG5, LocalState.builder()
            .actorState(new Jug5State(0))
            .build()));

    ActorActionConfig actorActionConfig = createActorActionConfig();

    StateMachineExecutionContext stateMachineExecutionContext =
        new StateMachineExecutionContext(initialState);
    StateMachine stateMachine =
        StateMachineFactory.create(new FourLiterVerifier(), createActorStateTransformerConfig());

    stateMachine.run(initialState, actorActionConfig, stateMachineExecutionContext);

    Report report = ReportGenerator.builder()
        .analyser(new JgraphtAnalyser())
        .graphExporter(DotGraphExporterFactory.create())
        .outputDirName("/tmp")
        .build()
        .generate(stateMachineExecutionContext.getData());

    log.info(report.toString());
    assertThat(report.getExecutionSummary()).isEqualTo(ExecutionSummary.builder()
        .numOfValidationFailingStates(0)
        .numOfNonSelfTransitions(1)
        .numOfStates(2)
        .numOfTransitions(4)
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
            .actionLabel("rejectSetOccupancy")
            .action(((jug3, unused) -> jug3.rejectActionFrom(JUG5,
                new SimulatedFailure("id",
                    obj -> ((Jug3) obj).setOccupancy(any(Integer.class)),
                    new RuntimeException()))))
            .build()
    );
  }

  private static Set<ActionTemplate<?, ?>> createJug5ActionTemplates() {
    return ImmutableSet.of(
        ActionTemplate.<Jug5, Jug3>builder()
            .actionPerformerId(JUG5)
            .actionType(new TransitiveActionType(JUG3))
            .actionLabel("addTo")
            .action(Jug5::addTo)
            .build()
    );
  }
}
