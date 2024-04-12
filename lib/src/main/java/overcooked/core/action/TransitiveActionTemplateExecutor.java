package overcooked.core.action;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateExtractor;
import overcooked.core.actor.ActorStateTransformerConfig;

/**
 * The object that is responsible for executing a transitive action template.
 */
@Builder
public class TransitiveActionTemplateExecutor {
  private final ActorStateTransformerConfig config;
  private final ActionTaker actionTaker;

  /**
   * Executes the action defined in the {@link ActionTemplate} object on behalf of the actor that
   * is defined in the {@link ActorId} object. The actor will be initialised from the
   * {@link ActorState} object.
   *
   * @param actionTemplate the template of the action that is going to be performed
   * @param actionPerformerState the state of the action performer
   * @param actionReceiverState  the state of the action receiver
   * @return an {@link ExecutionResult} object
   */
  public <PerformerT, ReceiverT> ExecutionResult execute(
      ActionTemplate<PerformerT, ReceiverT>  actionTemplate,
      ActorState actionPerformerState,
      ActorState actionReceiverState) {
    Preconditions.checkArgument(actionTemplate.getActionType().isTransitive(),
        "Expecting a transitive action template but it was intransitive {}", actionTemplate);

    ActorId actionPerformerId = actionTemplate.getActionPerformerId();

    PerformerT actionPerformer =
        this.<PerformerT>getActorFactory(actionTemplate.getActionPerformerId())
            .restoreFromActorState(actionPerformerState);

    ActorId actionReceiverId = actionTemplate.getActionType().getActionReceiverId();

    ReceiverT actionReceiver = this.<ReceiverT>getActorFactory(actionReceiverId)
        .restoreFromActorState(actionReceiverState);

    ActionResult actionResult = actionTaker.take(ActionDefinition.<PerformerT, ReceiverT>builder()
        .action(actionTemplate.getAction())
        .actionLabel(actionTemplate.getActionLabel())
        .actionPerformer(actionPerformer)
        .actionReceiver(actionReceiver)
        .build());

    return ExecutionResult.builder()
        .actionResult(actionResult)
        .localStates(ImmutableMap.<ActorId, ActorState>builder()
            .put(actionPerformerId,
                getActorStateExtractor(actionPerformerId)
                    .extract(actionPerformer))
            .put(actionReceiverId,
                getActorStateExtractor(actionReceiverId)
                    .extract(actionReceiver))
            .build())
        .build();
  }

  @SuppressWarnings("unchecked")
  private <ActorT> ActorStateExtractor<ActorT> getActorStateExtractor(
      ActorId actionPerformerId) {
    return (ActorStateExtractor<ActorT>) checkNotNull(
        config.getActorStateExtractors().get(actionPerformerId),
        "No ActorStateExtractor found for actor {}", actionPerformerId);
  }

  @SuppressWarnings("unchecked")
  private <ActorT> ActorFactory<ActorT> getActorFactory(ActorId actionReceiverId) {
    return (ActorFactory<ActorT>) checkNotNull(
        config.getActorFactories().get(actionReceiverId),
        "No ActorFactory found for actor {}", actionReceiverId);
  }
}
