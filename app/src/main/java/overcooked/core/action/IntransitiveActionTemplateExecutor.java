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
 * The object that is responsible for executing an intransitive action.
 */
@Builder
public class IntransitiveActionTemplateExecutor {
  private final ActorStateTransformerConfig config;
  private final IntransitiveActionTaker intransitiveActionTaker;

  /**
   * Executes the action defined in the {@link ActionTemplate} object on behalf of the actor that
   * is defined in the {@link ActorDefinition} object. The actor will be initialised from the
   * {@link LocalState} object.
   *
   * @param actorLocalState the local state of the actor which is going to perform the action
   * @param actorDefinition the definition of the actor
   * @param actionTemplate  the template of the action that is going to be performed
   * @return a map of {@link ActorDefinition} and {@link LocalState} representing the new local
   * states of the affected actors
   */
  public Map<ActorDefinition, LocalState> execute(LocalState actorLocalState,
                                                  ActorDefinition actorDefinition,
                                                  ActionTemplate actionTemplate) {
    Preconditions.checkArgument(!actionTemplate.getActionType().isTransitive(),
        "Expecting an intransitive action template but it was transitive {}", actionTemplate);

    Object actor = checkNotNull(config.getActorFactories().get(actorDefinition),
        "No ActorFactory found for actor {}", actorDefinition)
        .restoreFromLocalState(actorLocalState);

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
