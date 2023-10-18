package overcooked.core.action;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Builder
public class IntransitiveActionTemplateExecutor {
    private final ActorStateTransformerConfig config;
    private final IntransitiveActionTaker intransitiveActionTaker;

    public Map<ActorDefinition, LocalState> execute(LocalState actorlocalState,
                                                    ActorDefinition actorDefinition,
                                                    ActionTemplate actionTemplate) {
        Preconditions.checkArgument(!actionTemplate.getActionType().isTransitive(),
            "Expecting an intransitive action template but it was transitive {}", actionTemplate);

        Object actor = checkNotNull(config.getActorFactories().get(actorDefinition),
            "No ActorFactory found for actor {}", actorDefinition)
            .restoreFromLocalState(actorlocalState);

        intransitiveActionTaker.take(IntransitiveAction.builder()
            .actor(actor)
            .actionTemplate(actionTemplate)
            .build());

        return ImmutableMap.of(
            actorDefinition,
            checkNotNull(config.getLocalStateExtractors().get(actorDefinition),
                "No LocalStateExtractor found for actor {}", actorDefinition)
                .extract(actor));
    }
}
