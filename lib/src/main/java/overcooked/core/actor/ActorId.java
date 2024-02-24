package overcooked.core.actor;

import lombok.NonNull;
import lombok.Value;


/**
 * Represents the ID of an actor in the system to be model verified.
 */
@Value
public class ActorId {
  @NonNull
  String id;

  public String toString() {
    return id;
  }
}
