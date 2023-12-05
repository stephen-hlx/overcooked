package overcooked.core.action;

import lombok.Builder;
import lombok.Value;

/**
 * Describes a transitive action.
 */
@Builder
@Value
class TransitiveAction {
  Object actionPerformer;
  Object actionReceiver;
  ActionTemplate actionTemplate;
}
