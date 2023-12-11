package overcooked.core.action;

import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Describes an action, with its type, the method name as well as the parameters.
 */
@Builder
@Getter(AccessLevel.PACKAGE)
@EqualsAndHashCode
@Value
public class ActionDefinition<PerformerT, ReceiverT> {
  ActionType actionType;

  String methodName;
  BiConsumer<PerformerT, ReceiverT> action;

  ReceiverT actionReceiver;
}
