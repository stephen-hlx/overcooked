package overcooked.core.actor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represents the environment state of the Actor.
 * For instance, whether it is reachable, by some or all other actors.
 * <p/>
 * On the other hand, {@link ActorState} represents an actor's internal state.
 * TODO: can this be made package private?
 */
@Data
@AllArgsConstructor
@SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
    justification = "this is just for internal use, making it immutable is over engineering")
public class ActorEnvState {

  private final Map<ActorId, Set<SimulatedFailure>> rejections;

  public ActorEnvState() {
    this(new HashMap<>());
  }

  public ActorEnvState deepCopy() {
    Map<ActorId, Set<SimulatedFailure>> copy = new HashMap<>(this.rejections);
    return new ActorEnvState(copy);
  }
}
