package overcooked.core.action;

import java.util.function.BiConsumer;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

/**
 * The template of an action to be performed by an actor in the system to be model verified.
 */
@Builder
@Getter
@Value
public class ActionTemplate<ActionPerformerT, ActionReceiverT> {
  ActionType actionType;

  String methodName;
  BiConsumer<ActionPerformerT, ActionReceiverT> action;
}
