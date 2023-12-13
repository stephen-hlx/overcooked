package overcooked.core.action;

import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import overcooked.core.actor.Actor;

/**
 * Describes an action, with its type, the method name as well as the parameters.
 */
@Builder
@Getter(AccessLevel.PACKAGE)
@EqualsAndHashCode
@Value
public class ActionDefinition<PerformerT, ReceiverT> {
  Actor actionPerformerDefinition;
  ActionType actionType;
  String actionLabel;
  BiConsumer<PerformerT, ReceiverT> action;
  ReceiverT actionReceiver;
}
