package overcooked.core.action;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.Builder;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;

/**
 * The object that is responsible for executing a transitive action template.
 */
@Builder
public class TransitiveActionTemplateExecutor {
  private final ActorStateTransformerConfig config;
  private final TransitiveActionTaker transitiveActionTaker;

  /**
   * Executes the action defined in the {@link ActionTemplate} object on behalf of the actor that
   * is defined in the {@link ActorDefinition} object. The actor will be initialised from the
   * {@link LocalState} object.
   *
   * @param actionPerformerLocalState the local state of the action performer
   * @param actionPerformerDefinition the definition of the action performer
   * @param actionReceiverLocalState  the local state of the action receiver
   * @param actionTemplate            the template of the action that is going to be performed
   * @return a map of {@link ActorDefinition} and {@link LocalState} representing the new local
   *     states of the affected actors (performer and receiver)
   */
  public Map<ActorDefinition, LocalState> execute(LocalState actionPerformerLocalState,
                                                  ActorDefinition actionPerformerDefinition,
                                                  LocalState actionReceiverLocalState,
                                                  ActionTemplate actionTemplate) {
    Preconditions.checkArgument(actionTemplate.getActionType().isTransitive(),
        "Expecting a transitive action template but it was intransitive {}", actionTemplate);

    Object actionPerformer = checkNotNull(
        config.getActorFactories().get(actionPerformerDefinition),
        "No ActorFactory found for action performer {}", actionPerformerDefinition)
        .restoreFromLocalState(actionPerformerLocalState);

    ActorDefinition actionReceiverDefinition =
        actionTemplate.getActionType().getActionReceiverDefinition();

    Object actionReceiver = checkNotNull(
        config.getActorFactories().get(actionReceiverDefinition),
        "No ActorFactory found for action receiver {}", actionReceiverDefinition)
        .restoreFromLocalState(actionReceiverLocalState);

    transitiveActionTaker.take(TransitiveAction.builder()
        .actionPerformer(actionPerformer)
        .actionReceiver(actionReceiver)
        .actionTemplate(actionTemplate)
        .build());

    return ImmutableMap.<ActorDefinition, LocalState>builder()
        .put(actionPerformerDefinition,
            checkNotNull(
                config.getLocalStateExtractors().get(actionPerformerDefinition),
                "No LocalStateExtractor found for action performer {}", actionPerformerDefinition)
                .extract(actionPerformer))
        .put(actionReceiverDefinition,
            checkNotNull(
                config.getLocalStateExtractors().get(actionReceiverDefinition),
                "No LocalStateExtractor found for action receiver {}", actionReceiverDefinition)
                .extract(actionReceiver))
        .build();
  }
}
