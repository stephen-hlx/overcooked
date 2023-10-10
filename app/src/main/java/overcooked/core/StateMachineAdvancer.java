package overcooked.core;

import lombok.Builder;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.IntransitiveActionTemplateExecutor;
import overcooked.core.action.TransitiveActionTemplateExecutor;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

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
                                        ActorActionConfig actorActionConfig) {
        Set<GlobalState> nextStates = new HashSet<>();

        globalState.getLocalStates().forEach((actorDefinition, localState) ->
            actorActionConfig.getActionDefinitionTemplates().getOrDefault(actorDefinition, Collections.emptySet())
                .forEach(actionTemplate -> {

                    Map<ActorDefinition, LocalState> newLocalStates;
                    if (actionTemplate.getActionType().isTransitive()) {
                        newLocalStates = transitiveActionTemplateExecutor.execute(
                            globalState.getLocalStates().get(actorDefinition),
                            actorDefinition,
                            globalState.getLocalStates()
                                .get(actionTemplate.getActionType().getActionReceiverDefinition()),
                            actionTemplate);
                    } else {
                        newLocalStates = intransitiveActionTemplateExecutor.execute(
                            globalState.getLocalStates().get(actorDefinition),
                            actorDefinition,
                            actionTemplate);
                    }
                    nextStates.add(stateMerger.merge(globalState, newLocalStates));
                }));

        return nextStates;
    }
}
