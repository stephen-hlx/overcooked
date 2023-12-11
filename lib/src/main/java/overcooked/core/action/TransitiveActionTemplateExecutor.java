package overcooked.core.action;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import overcooked.core.actor.Actor;
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
   * is defined in the {@link Actor} object. The actor will be initialised from the
   * {@link LocalState} object.
   *
   * @param actionPerformerLocalState the local state of the action performer
   * @param actionPerformerDefinition the definition of the action performer
   * @param actionReceiverLocalState  the local state of the action receiver
   * @param actionTemplate            the template of the action that is going to be performed
   * @return an {@link ExecutionResult} object
   */
  public <PerformerT, ReceiverT> ExecutionResult execute(
      LocalState actionPerformerLocalState,
      Actor actionPerformerDefinition,
      LocalState actionReceiverLocalState,
      ActionTemplate<PerformerT, ReceiverT>  actionTemplate) {
    Preconditions.checkArgument(actionTemplate.getActionType().isTransitive(),
        "Expecting a transitive action template but it was intransitive {}", actionTemplate);

    Object actionPerformer = checkNotNull(
        config.getActorFactories().get(actionPerformerDefinition),
        "No ActorFactory found for action performer {}", actionPerformerDefinition)
        .restoreFromLocalState(actionPerformerLocalState);

    Actor actionReceiverDefinition =
        actionTemplate.getActionType().getActionReceiverDefinition();

    Object actionReceiver = checkNotNull(
        config.getActorFactories().get(actionReceiverDefinition),
        "No ActorFactory found for action receiver {}", actionReceiverDefinition)
        .restoreFromLocalState(actionReceiverLocalState);

    @SuppressWarnings("unchecked")
    ActionResult actionResult =
        transitiveActionTaker.take(TransitiveAction.<PerformerT, ReceiverT>builder()
            .actionPerformer((PerformerT) actionPerformer)
            .actionReceiver((ReceiverT) actionReceiver)
            .actionTemplate(actionTemplate)
            .build());

    return ExecutionResult.builder()
        .actionResult(actionResult)
        .localStates(ImmutableMap.<Actor, LocalState>builder()
            .put(actionPerformerDefinition,
                checkNotNull(
                    config.getLocalStateExtractors().get(actionPerformerDefinition),
                    "No LocalStateExtractor found for action performer {}",
                    actionPerformerDefinition)
                    .extract(actionPerformer))
            .put(actionReceiverDefinition,
                checkNotNull(
                    config.getLocalStateExtractors().get(actionReceiverDefinition),
                    "No LocalStateExtractor found for action receiver {}", actionReceiverDefinition)
                    .extract(actionReceiver))
            .build())
        .build();
  }
}
