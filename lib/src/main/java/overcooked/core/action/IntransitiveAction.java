package overcooked.core.action;

import lombok.Builder;
import lombok.Value;

/**
 * A class that models an intransitive action that is to be performed by an actor in the system
 * to be model verified.
 */
@Builder
@Value
class IntransitiveAction<PerformerT, ReceiverT> {
  PerformerT actor;
  ActionTemplate<PerformerT, ReceiverT> actionTemplate;
}
