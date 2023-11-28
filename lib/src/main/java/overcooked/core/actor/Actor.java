package overcooked.core.actor;

import lombok.Builder;
import lombok.Value;


/**
 * Definition of an actor of the system to be model verified.
 */
@Value
@Builder
public class Actor {
  String id;
}