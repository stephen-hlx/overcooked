package overcooked.analysis;

import lombok.Builder;
import lombok.Value;
import overcooked.core.actor.ActorId;

/**
 * An object representing an arc from one {@link overcooked.core.GlobalState} to another.
 */
@Builder
@Value
public class Arc {
  ActorId actionPerformerId;
  String label;
  ActorId actionReceiverId;
}
