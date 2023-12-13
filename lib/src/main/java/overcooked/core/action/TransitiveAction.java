package overcooked.core.action;

import lombok.Builder;
import lombok.Value;

/**
 * Describes a transitive action.
 */
@Builder
@Value
class TransitiveAction<PerformerT, ReceiverT> {
  PerformerT actionPerformer;
  ReceiverT actionReceiver;
  // TODO - only action is needed here
  ActionTemplate<PerformerT, ReceiverT> actionTemplate;
}
