package overcooked.core.action;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import overcooked.core.actor.Actor;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;

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

    PerformerT actionPerformer = this.<PerformerT>getActorFactory(actionPerformerDefinition)
        .restoreFromLocalState(actionPerformerLocalState);

    Actor actionReceiverDefinition =
        actionTemplate.getActionType().getActionReceiverDefinition();

    ReceiverT actionReceiver = this.<ReceiverT>getActorFactory(actionReceiverDefinition)
        .restoreFromLocalState(actionReceiverLocalState);

    ActionResult actionResult =
        transitiveActionTaker.take(TransitiveAction.<PerformerT, ReceiverT>builder()
            .actionPerformer(actionPerformer)
            .actionReceiver(actionReceiver)
            .actionTemplate(actionTemplate)
            .build());

    return ExecutionResult.builder()
        .actionResult(actionResult)
        .localStates(ImmutableMap.<Actor, LocalState>builder()
            .put(actionPerformerDefinition,
                getLocalStateExtractor(actionPerformerDefinition)
                    .extract(actionPerformer))
            .put(actionReceiverDefinition,
                getLocalStateExtractor(actionReceiverDefinition)
                    .extract(actionReceiver))
            .build())
        .build();
  }

  @SuppressWarnings("unchecked")
  private <ActorT> LocalStateExtractor<ActorT> getLocalStateExtractor(
      Actor actionPerformerDefinition) {
    return (LocalStateExtractor<ActorT>) checkNotNull(
        config.getLocalStateExtractors().get(actionPerformerDefinition),
        "No LocalStateExtractor found for actor {}",
        actionPerformerDefinition);
  }

  @SuppressWarnings("unchecked")
  private <ActorT> ActorFactory<ActorT> getActorFactory(Actor actionReceiverDefinition) {
    return (ActorFactory<ActorT>) checkNotNull(
        config.getActorFactories().get(actionReceiverDefinition),
        "No ActorFactory found for actor {}", actionReceiverDefinition);
  }
}
