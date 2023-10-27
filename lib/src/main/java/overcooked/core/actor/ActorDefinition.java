package overcooked.core.actor;

import lombok.Builder;
import lombok.Value;


/**
 * Definition of an actor of the system to be model verified.
 * TODO equals should use id only
 */
@Value
@Builder
public class ActorDefinition {
  String id;
  Class<?> type;
}
