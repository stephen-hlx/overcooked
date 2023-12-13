package overcooked.core.action;

import java.util.function.BiConsumer;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import overcooked.core.actor.Actor;

/**
 * The template of an action to be performed by an actor in the system to be model verified.
 */
@Builder
@Getter
@Value
public class ActionTemplate<ActionPerformerT, ActionReceiverT> {
  @NonNull
  Actor actionPerformerDefinition;
  @NonNull
  ActionType actionType;
  @NonNull
  String actionLabel;
  @NonNull
  BiConsumer<ActionPerformerT, ActionReceiverT> action;
}
