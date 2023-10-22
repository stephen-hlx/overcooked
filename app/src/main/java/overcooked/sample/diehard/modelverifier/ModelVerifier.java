package overcooked.sample.diehard.modelverifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import lombok.extern.java.Log;
import overcooked.analysis.GraphDataCollector;
import overcooked.core.ActorActionConfig;
import overcooked.core.GlobalState;
import overcooked.core.StateMachine;
import overcooked.core.StateMachineFactory;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.IntransitiveActionType;
import overcooked.core.action.ParamTemplate;
import overcooked.core.action.TransitiveActionType;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.sample.diehard.model.Jar3;
import overcooked.sample.diehard.model.Jar5;
import overcooked.visual.DotGraphBuilder;
import overcooked.visual.GlobalStatePrinter;
import overcooked.visual.TransitionPrinter;

/**
 * The ModelVerifier of example diehard.
 */
@Log
public class ModelVerifier {
  private static final ActorDefinition JAR3 = ActorDefinition.builder()
      .id("jar3")
      .type(Jar3.class)
      .localStateType(Jar3State.class)
      .build();
  private static final ActorDefinition JAR5 = ActorDefinition.builder()
      .id("jar5")
      .type(Jar5.class)
      .localStateType(Jar5State.class)
      .build();

  /**
   * Main function of the model viewer.
   *
   * @param args args of main function
   */
  public static void main(String[] args) {
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
            .actionType(new TransitiveActionType(ActorDefinition.builder()
                .id("jar5")
                .type(Jar5.class)
                .localStateType(Jar5State.class)
                .build()))
            .methodName("addTo")
            .parameters(ImmutableList.of(new ParamTemplate<>(Jar5.class)))
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
            .actionType(new TransitiveActionType(ActorDefinition.builder()
                .id("jar3")
                .type(Jar3.class)
                .localStateType(Jar3State.class)
                .build()))
            .methodName("addTo")
            .parameters(ImmutableList.of(new ParamTemplate<>(Jar3.class)))
            .build()
    );

    ActorActionConfig actorActionConfig = new ActorActionConfig(ImmutableMap.of(
        JAR3, jar3Templates,
        JAR5, jar5Templates
    ));

    GraphDataCollector graphDataCollector = new GraphDataCollector(initialState);
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

    stateMachine.run(initialState, actorActionConfig, graphDataCollector);

    DotGraphBuilder dotGraphBuilder =
        new DotGraphBuilder(new TransitionPrinter(new GlobalStatePrinter()));
    log.info(dotGraphBuilder.build(graphDataCollector.getTransitions()));
    log.info(String.valueOf(graphDataCollector.getValidationFailingGlobalStates().size()));
    graphDataCollector.getValidationFailingGlobalStates().forEach(gs -> log.info(gs.toString()));
  }
}
