package overcooked.sample.twophasecommit.modelverifier;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.java.Log;
import overcooked.analysis.Analyser;
import overcooked.analysis.JgraphtAnalyser;
import overcooked.analysis.StateMachineExecutionData;
import overcooked.analysis.StateMachineExecutionDataCollector;
import overcooked.core.ActorActionConfig;
import overcooked.core.GlobalState;
import overcooked.core.StateMachine;
import overcooked.core.StateMachineFactory;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.IntransitiveActionType;
import overcooked.core.action.ParamTemplate;
import overcooked.core.action.TransitiveActionType;
import overcooked.core.actor.Actor;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;
import overcooked.visual.DotGraphExporter;
import overcooked.visual.DotGraphExporterFactory;

@Log
class ModelVerifier {
  private static final String TM_ID = "TM";
  private static final Actor TM = Actor.builder()
      .id(TM_ID)
      .build();

  private static final String RM_ID_0 = "0";
  private static final String RM_ID_1 = "1";
  private static final String RM_ID_2 = "2";
  private static final Actor RM_0 = Actor.builder()
      .id(RM_ID_0)
      .build();

  private static final Actor RM_1 = Actor.builder()
      .id(RM_ID_1)
      .build();

  private static final Actor RM_2 = Actor.builder()
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

  public void verify() {
    StateMachineExecutionDataCollector stateMachineExecutionDataCollector =
        new StateMachineExecutionDataCollector(initialGlobalState);
    StateMachine stateMachine = StateMachineFactory
        .create(new TransactionStateVerifier(TM_ID), actorStateTransformerConfig);

    stateMachine.run(initialGlobalState, actorActionConfig, stateMachineExecutionDataCollector);

    StateMachineExecutionData executionData = stateMachineExecutionDataCollector.getData();

    DotGraphExporter dotGraphExporter = DotGraphExporterFactory.create();

    Analyser analyser = new JgraphtAnalyser();
    log.info(dotGraphExporter.export(executionData.getTransitions()));
    executionData.getValidationFailingGlobalStates().forEach(failingState ->
        log.info(dotGraphExporter.export(
            ImmutableSet.copyOf(
                analyser.findShortestPathToFailureState(initialGlobalState,
                    failingState,
                    executionData.getTransitions())))));
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
    Set<ActionTemplate> resourceManagerActionTemplates = resourceManagerActionTemplates();
    return new ActorActionConfig(ImmutableMap.of(
        TM, transactionManagerActionTemplates(),
        RM_0, resourceManagerActionTemplates,
        RM_1, resourceManagerActionTemplates,
        RM_2, resourceManagerActionTemplates
    ));
  }

  private static ActorStateTransformerConfig actorStateTransformerConfig() {
    ResourceManagerFactory resourceManagerFactory = new ResourceManagerFactory();
    ResourceManagerLocalStateExtractor resourceManagerLocalStateExtractor =
        new ResourceManagerLocalStateExtractor();
    return ActorStateTransformerConfig.builder()
        .actorFactories(ImmutableMap.of(
            TM, new TransactionManagerFactory(),
            RM_0, resourceManagerFactory,
            RM_1, resourceManagerFactory,
            RM_2, resourceManagerFactory
        ))
        .localStateExtractors(ImmutableMap.of(
            TM, new TransactionManagerLocalStateExtractor(),
            RM_0, resourceManagerLocalStateExtractor,
            RM_1, resourceManagerLocalStateExtractor,
            RM_2, resourceManagerLocalStateExtractor
        ))
        .build();
  }

  private static ImmutableSet<ActionTemplate> transactionManagerActionTemplates() {
    return ImmutableSet.of(
        ActionTemplate.builder()
            .actionType(new TransitiveActionType(Actor.builder()
                .id(RM_ID_0)
                .build()))
            .methodName("abort")
            .parameters(ImmutableList.of(new ParamTemplate<>(ResourceManagerClient.class)))
            .build(),
        ActionTemplate.builder()
            .actionType(new TransitiveActionType(Actor.builder()
                .id(RM_ID_0)
                .build()))
            .methodName("commit")
            .parameters(ImmutableList.of(new ParamTemplate<>(ResourceManagerClient.class)))
            .build(),
        ActionTemplate.builder()
            .actionType(new TransitiveActionType(Actor.builder()
                .id(RM_ID_1)
                .build()))
            .methodName("abort")
            .parameters(ImmutableList.of(new ParamTemplate<>(ResourceManagerClient.class)))
            .build(),
        ActionTemplate.builder()
            .actionType(new TransitiveActionType(Actor.builder()
                .id(RM_ID_1)
                .build()))
            .methodName("commit")
            .parameters(ImmutableList.of(new ParamTemplate<>(ResourceManagerClient.class)))
            .build(),
        ActionTemplate.builder()
            .actionType(new TransitiveActionType(Actor.builder()
                .id(RM_ID_2)
                .build()))
            .methodName("abort")
            .parameters(ImmutableList.of(new ParamTemplate<>(ResourceManagerClient.class)))
            .build(),
        ActionTemplate.builder()
            .actionType(new TransitiveActionType(Actor.builder()
                .id(RM_ID_2)
                .build()))
            .methodName("commit")
            .parameters(ImmutableList.of(new ParamTemplate<>(ResourceManagerClient.class)))
            .build()
    );
  }

  private static ImmutableSet<ActionTemplate> resourceManagerActionTemplates() {
    return ImmutableSet.of(
        ActionTemplate.builder()
            .actionType(new TransitiveActionType(Actor.builder()
                .id(TM_ID)
                .build()))
            .methodName("abort")
            .parameters(ImmutableList.of(new ParamTemplate<>(TransactionManagerClient.class)))
            .build(),
        ActionTemplate.builder()
            .actionType(new TransitiveActionType(Actor.builder()
                .id(TM_ID)
                .build()))
            .methodName("prepare")
            .parameters(ImmutableList.of(new ParamTemplate<>(TransactionManagerClient.class)))
            .build()
    );
  }
}
