package overcooked.core.action;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;

/**
 * A structure that provides not only the local states that corresponds to the actors involved in
 * the action, but also the action result.
 */
@Value
@Builder
@SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
    justification = "this is merely an internal structure")
public class ExecutionResult {
  ActionResult actionResult;
  Map<ActorId, ActorState> localStates;
}
