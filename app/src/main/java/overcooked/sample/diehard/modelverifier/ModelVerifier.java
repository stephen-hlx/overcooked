package overcooked.sample.diehard.modelverifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.extern.java.Log;
import overcooked.core.*;
import overcooked.core.action.*;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.analysis.Analyser;
import overcooked.core.visual.DotGraphBuilder;
import overcooked.core.visual.GlobalStatePrinter;
import overcooked.core.visual.TransitionPrinter;
import overcooked.sample.diehard.model.Jar3;
import overcooked.sample.diehard.model.Jar5;

import java.util.Set;

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
    public static void main(String[] args) {
        GlobalState globalState = new GlobalState(ImmutableMap.of(
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

        ActorStateTransformerConfig actorStateTransformerConfig = ActorStateTransformerConfig.builder()
            .actorFactories(ImmutableMap.of(
                JAR3, new Jar3Factory(),
                JAR5, new Jar5Factory()
            ))
            .localStateExtractors(ImmutableMap.of(
                JAR3, new Jar3LocalStateExtractor(),
                JAR5, new Jar5LocalStateExtractor()
            ))
            .build();

        ActionTaker actionTaker = new ActionTaker();
        ActionTemplateMaterialiser materialiser = new ActionTemplateMaterialiser();

        StateMachineAdvancer stateMachineAdvancer = StateMachineAdvancer.builder()
            .stateMerger(new StateMerger())
            .intransitiveActionTemplateExecutor(IntransitiveActionTemplateExecutor.builder()
                .config(actorStateTransformerConfig)
                .intransitiveActionTaker(IntransitiveActionTaker.builder()
                    .actionTaker(actionTaker)
                    .materialiser(materialiser)
                    .build())
                .build())
            .transitiveActionTemplateExecutor(TransitiveActionTemplateExecutor.builder()
                .config(actorStateTransformerConfig)
                .transitiveActionTaker(TransitiveActionTaker.builder()
                    .actionTaker(actionTaker)
                    .materialiser(materialiser)
                    .build())
                .build())
            .build();
        Analyser analyser = new Analyser();
        StateMachine stateMachine = StateMachine.builder()
            .globalStateVerifier(new FourLiterVerifier())
            .stateMachineAdvancer(stateMachineAdvancer)
            .build();

        stateMachine.run(globalState, actorActionConfig, analyser);

        DotGraphBuilder dotGraphBuilder = new DotGraphBuilder(new TransitionPrinter(new GlobalStatePrinter()));
        log.info(dotGraphBuilder.build(analyser.getTransitions()));
        log.info(String.valueOf(analyser.getValidationFailingGlobalStates().size()));
        analyser.getValidationFailingGlobalStates().forEach(gs -> log.info(gs.toString()));
    }
}
