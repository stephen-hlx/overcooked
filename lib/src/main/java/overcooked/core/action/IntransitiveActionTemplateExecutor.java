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
 * The object that is responsible for executing an intransitive action.
 */
@Builder
public class IntransitiveActionTemplateExecutor {
  private final ActorStateTransformerConfig config;
  private final IntransitiveActionTaker intransitiveActionTaker;

  /**
   * Executes the action defined in the {@link ActionTemplate} object on behalf of the actor that
   * is defined in the {@link Actor} object. The actor will be initialised from the
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

    Actor actionPerformerDefinition = actionTemplate.getActionPerformerDefinition();
    @SuppressWarnings("unchecked")
    ActorFactory<PerformerT> actorFactory =
        (ActorFactory<PerformerT>) checkNotNull(
            config.getActorFactories().get(actionPerformerDefinition),
            "No ActorFactory found for actor {}", actionPerformerDefinition);
    PerformerT actionPerformer = actorFactory
        .restoreFromLocalState(actorLocalState);

    ActionResult actionResult = intransitiveActionTaker.take(
        IntransitiveAction.<PerformerT, ReceiverT>builder()
            .actor(actionPerformer)
            .actionTemplate(actionTemplate)
            .build());

    @SuppressWarnings("unchecked")
    LocalStateExtractor<PerformerT> localStateExtractor =
        (LocalStateExtractor<PerformerT>) checkNotNull(
            config.getLocalStateExtractors().get(actionPerformerDefinition),
            "No LocalStateExtractor found for actor {}", actionPerformerDefinition);

    return ExecutionResult.builder()
        .actionResult(actionResult)
        .localStates(ImmutableMap.of(
            actionPerformerDefinition, localStateExtractor.extract(actionPerformer)))
        .build();
  }
}
