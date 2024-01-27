package overcooked.sample.twophasecommit.modelverifier;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import overcooked.analysis.JgraphtAnalyser;
import overcooked.analysis.Report;
import overcooked.analysis.ReportGenerator;
import overcooked.analysis.TransitionFilter;
import overcooked.core.ActorActionConfig;
import overcooked.core.GlobalState;
import overcooked.core.StateMachine;
import overcooked.core.StateMachineExecutionContext;
import overcooked.core.StateMachineFactory;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.TransitiveActionType;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.visual.DotGraphExporterFactory;

@Slf4j
class ModelVerifier {
  private static final String TM_ID = "TM";
  private static final ActorId TM = ActorId.builder()
      .id(TM_ID)
      .build();

  private static final String RM_ID_0 = "RM0";
  private static final String RM_ID_1 = "RM1";
  private static final String RM_ID_2 = "RM2";
  private static final ActorId RM_0 = ActorId.builder()
      .id(RM_ID_0)
      .build();

  private static final ActorId RM_1 = ActorId.builder()
      .id(RM_ID_1)
      .build();

  private static final ActorId RM_2 = ActorId.builder()
      .id(RM_ID_2)
      .build();

  private final GlobalState initialGlobalState;
  private final ActorActionConfig actorActionConfig;

  private final ActorStateTransformerConfig actorStateTransformerConfig;

  ModelVerifier() {
    initialGlobalState = initialGlobalState();
    actorActionConfig = actorActionConfig();
    actorStateTransformerConfig = actorStateTransformerConfig();
  }

  public Report run() {
    StateMachineExecutionContext stateMachineExecutionContext =
        new StateMachineExecutionContext(initialGlobalState);
    StateMachine stateMachine = StateMachineFactory
        .create(new TransactionStateVerifier(TM_ID), actorStateTransformerConfig);

    stateMachine.run(initialGlobalState, actorActionConfig, stateMachineExecutionContext);

    String outputDirName = "/tmp/twophasecommit/" + System.currentTimeMillis();
    mkdir(outputDirName);
    ReportGenerator reportGenerator = ReportGenerator.builder()
        .analyser(new JgraphtAnalyser())
        .dotGraphExporter(DotGraphExporterFactory.create())
        .outputDirName(outputDirName)
        .transitionFilter(TransitionFilter.EXCEPTION_FREE.and(TransitionFilter.NON_SELF_LOOP))
        .build();
    return reportGenerator.generate(stateMachineExecutionContext.getData());
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
        TM, new TransactionManagerLocalState(resourceManagerStates),
        RM_0, new ResourceManagerLocalState(RM_ID_0, WORKING),
        RM_1, new ResourceManagerLocalState(RM_ID_1, WORKING),
        RM_2, new ResourceManagerLocalState(RM_ID_2, WORKING)
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
    ResourceManagerActorLocalStateExtractor resourceManagerActorLocalStateExtractor =
        new ResourceManagerActorLocalStateExtractor();
    return ActorStateTransformerConfig.builder()
        .actorFactories(ImmutableMap.of(
            TM, new TransactionManagerFactory(),
            RM_0, resourceManagerFactory,
            RM_1, resourceManagerFactory,
            RM_2, resourceManagerFactory
        ))
        .localStateExtractors(ImmutableMap.of(
            TM, new TransactionManagerActorLocalStateExtractor(),
            RM_0, resourceManagerActorLocalStateExtractor,
            RM_1, resourceManagerActorLocalStateExtractor,
            RM_2, resourceManagerActorLocalStateExtractor
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
      transactionManagerActions(
          ActorId resourceManagerId) {
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
