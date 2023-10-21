package overcooked.core.action;

import lombok.Builder;
import lombok.Value;

/**
 * Describes a transitive action.
 */
@Builder
@Value
public class TransitiveAction {
  Object actionPerformer;
  Class<?> actionReceiverType;
  Object actionReceiver;
  ActionTemplate actionTemplate;
}
