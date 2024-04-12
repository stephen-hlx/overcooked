package overcooked.sample.twophasecommit.modelverifier;

import static org.assertj.core.api.Assertions.assertThat;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import overcooked.analysis.ExecutionSummary;
import overcooked.analysis.JgraphtAnalyser;
import overcooked.analysis.Report;
import overcooked.analysis.ReportGenerator;
import overcooked.analysis.TransitionFilter;
import overcooked.core.ActorActionConfig;
import overcooked.core.GlobalState;
import overcooked.core.ModelVerifier;
import overcooked.core.StateMachineExecutionContext;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.TransitiveActionType;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.io.DotGraphExporterFactory;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

@Slf4j
class TwoPhaseCommitModelVerifierTest {
  private static final String TM_ID = "TM";
  private static final ActorId TM = new ActorId(TM_ID);

  private static final String RM_ID_0 = "RM0";
  private static final String RM_ID_1 = "RM1";
  private static final String RM_ID_2 = "RM2";
  private static final ActorId RM_0 = new ActorId(RM_ID_0);
  private static final ActorId RM_1 = new ActorId(RM_ID_1);
  private static final ActorId RM_2 = new ActorId(RM_ID_2);

  @Test
  void can_run_without_error() {
    ModelVerifier modelVerifier = ModelVerifier.builder()
        .actorActionConfig(actorActionConfig())
        .actorStateTransformerConfig(actorStateTransformerConfig())
        .invariantVerifier(new TransactionStateVerifier(TM))
        .build();

    StateMachineExecutionContext stateMachineExecutionContext =
        modelVerifier.runWith(initialGlobalState());

    String outputDirName = "/tmp/twophasecommit/" + System.currentTimeMillis();
    mkdir(outputDirName);
    ReportGenerator reportGenerator = ReportGenerator.builder()
        .analyser(new JgraphtAnalyser())
        .graphExporter(DotGraphExporterFactory.create())
        .outputDirName(outputDirName)
        .transitionFilter(TransitionFilter.EXCEPTION_FREE.and(TransitionFilter.NON_SELF_LOOP))
        .build();
    Report report = reportGenerator.generate(stateMachineExecutionContext.getData());
    log.info(report.toString());
    assertThat(report.getExecutionSummary()).isEqualTo(ExecutionSummary.builder()
            .numOfValidationFailingStates(0)
            .numOfNonSelfTransitions(120)
            .numOfStates(34)
            .numOfTransitions(408)
        .build());
  }

  @SuppressFBWarnings(value = "RV_RETURN_VALUE_IGNORED")
  private static void mkdir(String dirName) {
    log.info("Making dir " + dirName);
    new File(dirName).mkdirs();
  }

  private static GlobalState initialGlobalState() {
    Map<String, ResourceManagerState> resourceManagerStates = new HashMap<>();
    resourceManagerStates.put(RM_ID_0, WORKING);
    resourceManagerStates.put(RM_ID_1, WORKING);
    resourceManagerStates.put(RM_ID_2, WORKING);
    return new GlobalState(ImmutableMap.of(
        TM, new TransactionManagerActorState(resourceManagerStates),
        RM_0, new ResourceManagerActorState(RM_ID_0, WORKING),
        RM_1, new ResourceManagerActorState(RM_ID_1, WORKING),
        RM_2, new ResourceManagerActorState(RM_ID_2, WORKING)
    ));
  }

  private static ActorActionConfig actorActionConfig() {
    return new ActorActionConfig(ImmutableMap.of(
        TM, transactionManagerActionTemplates(),
        RM_0, resourceManagerActionTemplates(RM_0),
        RM_1, resourceManagerActionTemplates(RM_1),
        RM_2, resourceManagerActionTemplates(RM_2)
    ));
  }

  private static ActorStateTransformerConfig actorStateTransformerConfig() {
    ResourceManagerFactory resourceManagerFactory = new ResourceManagerFactory();
    ResourceManagerActorStateExtractor resourceManagerActorStateExtractor =
        new ResourceManagerActorStateExtractor();
    return ActorStateTransformerConfig.builder()
        .actorFactories(ImmutableMap.of(
            TM, new TransactionManagerFactory(),
            RM_0, resourceManagerFactory,
            RM_1, resourceManagerFactory,
            RM_2, resourceManagerFactory
        ))
        .actorStateExtractors(ImmutableMap.of(
            TM, new TransactionManagerActorStateExtractor(),
            RM_0, resourceManagerActorStateExtractor,
            RM_1, resourceManagerActorStateExtractor,
            RM_2, resourceManagerActorStateExtractor
        ))
        .build();
  }

  private static Set<ActionTemplate<?, ?>> transactionManagerActionTemplates() {
    return ImmutableSet.<ActionTemplate<?, ?>>builder()
        .addAll(transactionManagerActions(RM_0))
        .addAll(transactionManagerActions(RM_1))
        .addAll(transactionManagerActions(RM_2))
        .build();
  }

  private static Set<ActionTemplate<TransactionManagerActor, ResourceManagerActor>>
      transactionManagerActions(ActorId resourceManagerId) {
    return ImmutableSet.of(
        ActionTemplate.<TransactionManagerActor, ResourceManagerActor>builder()
            .actionPerformerId(TM)
            .actionType(new TransitiveActionType(resourceManagerId))
            .actionLabel("abort")
            .action(TransactionManagerActor::abort)
            .build(),
        ActionTemplate.<TransactionManagerActor, ResourceManagerActor>builder()
            .actionPerformerId(TM)
            .actionType(new TransitiveActionType(resourceManagerId))
            .actionLabel("commit")
            .action(TransactionManagerActor::commit)
            .build());
  }

  private static ImmutableSet<ActionTemplate<?, ?>> resourceManagerActionTemplates(
      ActorId actionPerformerId) {
    return ImmutableSet.of(
        ActionTemplate.<ResourceManagerActor, TransactionManagerActor>builder()
            .actionPerformerId(actionPerformerId)
            .actionType(new TransitiveActionType(TM))
            .actionLabel("selfAbort")
            .action(ResourceManagerActor::selfAbort)
            .build(),
        ActionTemplate.<ResourceManagerActor, TransactionManagerActor>builder()
            .actionPerformerId(actionPerformerId)
            .actionType(new TransitiveActionType(TM))
            .actionLabel("prepare")
            .action(ResourceManagerActor::prepare)
            .build()
    );
  }
}