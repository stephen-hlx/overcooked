package overcooked.core;

import lombok.Builder;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.IntransitiveActionTemplateExecutor;
import overcooked.core.action.TransitiveActionTemplateExecutor;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;
import overcooked.core.tracing.Tracer;
import overcooked.core.tracing.Transition;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Builder
public class StateMachineAdvancer {
    private final IntransitiveActionTemplateExecutor intransitiveActionTemplateExecutor;
    private final TransitiveActionTemplateExecutor transitiveActionTemplateExecutor;
    private final StateMerger stateMerger;

    public Set<GlobalState> computeNext(GlobalState globalState,
                                        ActorActionConfig actorActionConfig,
                                        Tracer tracer) {
        Set<GlobalState> nextStates = new HashSet<>();

        globalState.getLocalStates().forEach((actorDefinition, localState) ->
            actorActionConfig.getActionDefinitionTemplates().getOrDefault(actorDefinition, Collections.emptySet())
                .forEach(actionTemplate -> {
                    Transition.TransitionBuilder transitionBuilder = Transition.builder()
                        .from(globalState)
                        .actionPerformerId(actorDefinition.getId())
                        .methodName(actionTemplate.getMethodName());
                    Map<ActorDefinition, LocalState> newLocalStates;
                    if (actionTemplate.getActionType().isTransitive()) {
                        ActorDefinition actionReceiverDefinition =
                            actionTemplate.getActionType().getActionReceiverDefinition();
                        transitionBuilder.actionReceiverId(actionReceiverDefinition.getId());
                        newLocalStates = transitiveActionTemplateExecutor.execute(
                            globalState.getLocalStates().get(actorDefinition),
                            actorDefinition,
                            globalState.getLocalStates()
                                .get(actionReceiverDefinition),
                            actionTemplate);
                    } else {
                        newLocalStates = intransitiveActionTemplateExecutor.execute(
                            globalState.getLocalStates().get(actorDefinition),
                            actorDefinition,
                            actionTemplate);
                    }
                    GlobalState newGlobalState = stateMerger.merge(globalState, newLocalStates);
                    transitionBuilder.to(newGlobalState);
                    tracer.capture(transitionBuilder.build());
                    nextStates.add(newGlobalState);
                }));

        return nextStates;
    }
}
