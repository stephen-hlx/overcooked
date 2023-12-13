package overcooked.core.action;

import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

/**
 * Describes an action, with its type, the method name as well as the parameters.
 */
@Builder
@Getter(AccessLevel.PACKAGE)
@EqualsAndHashCode(exclude = "action")
@Value
public class ActionDefinition<PerformerT, ReceiverT> {
  @NonNull
  String actionLabel;
  @NonNull
  BiConsumer<PerformerT, ReceiverT> action;
  @NonNull
  PerformerT actionPerformer;
  ReceiverT actionReceiver;
}
