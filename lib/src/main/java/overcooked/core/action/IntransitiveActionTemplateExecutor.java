package overcooked.core.action;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorStateTransformerConfig;
import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;

/**
 * The object that is responsible for executing an intransitive action.
 */
@Builder
public class IntransitiveActionTemplateExecutor {
  private final ActorStateTransformerConfig config;
  private final ActionTaker actionTaker;

  /**
   * Executes the action defined in the {@link ActionTemplate} object on behalf of the actor that
   * is defined in the {@link ActorId} object. The actor will be initialised from the
   * {@link LocalState} object.
   *
   * @param actorLocalState the local state of the actor which is going to perform the action
   * @param actionTemplate  the template of the action that is going to be performed
   * @return an {@link ExecutionResult} object
   */
  public <PerformerT, ReceiverT> ExecutionResult execute(
      ActionTemplate<PerformerT, ReceiverT> actionTemplate,
      LocalState actorLocalState) {
    Preconditions.checkArgument(!actionTemplate.getActionType().isTransitive(),
        "Expecting an intransitive action template but it was transitive {}", actionTemplate);

    ActorId actionPerformerId = actionTemplate.getActionPerformerId();
    @SuppressWarnings("unchecked")
    ActorFactory<PerformerT> actorFactory =
        (ActorFactory<PerformerT>) checkNotNull(
            config.getActorFactories().get(actionPerformerId),
            "No ActorFactory found for actor {}", actionPerformerId);
    PerformerT actionPerformer = actorFactory
        .restoreFromLocalState(actorLocalState);

    ActionResult actionResult = actionTaker.take(ActionDefinition.<PerformerT, ReceiverT>builder()
        .action(actionTemplate.getAction())
        .actionLabel(actionTemplate.getActionLabel())
        .actionPerformer(actionPerformer)
        .actionReceiver(null)
        .build());

    @SuppressWarnings("unchecked")
    LocalStateExtractor<PerformerT> localStateExtractor =
        (LocalStateExtractor<PerformerT>) checkNotNull(
            config.getLocalStateExtractors().get(actionPerformerId),
            "No LocalStateExtractor found for actor {}", actionPerformerId);

    return ExecutionResult.builder()
        .actionResult(actionResult)
        .localStates(ImmutableMap.of(
            actionPerformerId, localStateExtractor.extract(actionPerformer)))
        .build();
  }
}
