package overcooked.core.action;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;

import java.util.Map;

@Builder
public class IntransitiveActionTemplateExecutor {
    private final ActorStateTransformerConfig config;
    private final IntransitiveActionTaker intransitiveActionTaker;

    public Map<ActorDefinition, LocalState> execute(LocalState actorlocalState,
                                                    ActorDefinition actorDefinition,
                                                    ActionTemplate actionTemplate) {
        Preconditions.checkArgument(!actionTemplate.getActionType().isTransitive(),
            "Expecting an intransitive action template but it was transitive {}", actionTemplate);

        Object actor = config.getActorFactories()
            .get(actorDefinition)
            .restoreFromLocalState(actorlocalState);

        intransitiveActionTaker.take(IntransitiveAction.builder()
            .actor(actor)
            .actionTemplate(actionTemplate)
            .build());

        return ImmutableMap.of(
            actorDefinition,
            config.getLocalStateExtractors().get(actorDefinition).extract(actor));
    }
}
