package overcooked.core.action;

import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;

/**
 * The object that is responsible for taking an action for an actor.
 */
@Slf4j
class ActionTaker {
  /**
   * Takes the action defined in the {@link ActionDefinition} object on behalf of the actor.
   *
   * @param actor            the actor
   * @param actionDefinition the action to be performed
   */
  public <PerformerT, ReceiverT> ActionResult take(
      PerformerT actor,
      ActionDefinition<PerformerT, ReceiverT> actionDefinition) {
    BiConsumer<PerformerT, ReceiverT> action = actionDefinition.getAction();
    ReceiverT actionReceiver = actionDefinition.getActionReceiver();
    try {
      action.accept(actor, actionReceiver);
    } catch (Exception e) {
      log.info(String.format("When %s.%s is called against %s Exception %s was thrown",
          actor.getClass().getSimpleName(),
          actionDefinition.getMethodName(),
          actionReceiverName(actionReceiver),
          e));
      return ActionResult.failure(e);
    }

    return ActionResult.success();
  }

  private static <ReceiverT> String actionReceiverName(ReceiverT actionReceiver) {
    return actionReceiver == null ? "itself" : actionReceiver.toString();
  }
}
