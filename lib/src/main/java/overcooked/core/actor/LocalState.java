package overcooked.core.actor;

import com.google.gson.Gson;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Value;

/**
 * The state of an actor, including its environment state that is not part of
 * its internal state, e.g. whether it is still alive, or whether it can
 * communicate with (some of) the other actors.
 */
@Value
@Builder
@SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
    justification = "this is just for internal use, making it immutable is over engineering")
public class LocalState {
  private static final Gson GSON = new Gson();

  ActorState actorState;
  @Builder.Default
  ActorEnvState actorEnvState = new ActorEnvState();

  public LocalState deepCopy() {
    return new LocalState(actorState.deepCopy(), actorEnvState.deepCopy());
  }
}
