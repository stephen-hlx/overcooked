package overcooked.core.action;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;

import java.util.Map;

@Builder
public class TransitiveActionTemplateExecutor {
    private final ActorStateTransformerConfig config;
    private final TransitiveActionTaker transitiveActionTaker;

    public Map<ActorDefinition, LocalState> execute(LocalState actionPerformerLocalState,
                                                    ActorDefinition actionPerformerDefinition,
                                                    LocalState actionReceiverLocalState,
                                                    ActionTemplate actionTemplate) {
        Preconditions.checkArgument(actionTemplate.getActionType().isTransitive(),
            "Expecting a transitive action template but it was intransitive {}", actionTemplate);

        Object actionPerformer = config.getActorFactories()
            .get(actionPerformerDefinition)
            .restoreFromLocalState(actionPerformerLocalState);

        ActorDefinition actionReceiverDefinition = actionTemplate.getActionType().getActionReceiverDefinition();

        Object actionReceiver = config.getActorFactories()
            .get(actionReceiverDefinition)
            .restoreFromLocalState(actionReceiverLocalState);

        transitiveActionTaker.take(TransitiveAction.builder()
            .actionPerformer(actionPerformer)
            .actionReceiver(actionReceiver)
            .actionTemplate(actionTemplate)
            .build());

        return ImmutableMap.<ActorDefinition, LocalState>builder()
            .put(actionPerformerDefinition,
                config.getLocalStateExtractors().get(actionPerformerDefinition).extract(actionPerformer))
            .put(actionReceiverDefinition,
                config.getLocalStateExtractors().get(actionReceiverDefinition).extract(actionReceiver))
            .build();
    }
}
