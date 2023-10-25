package overcooked.analysis;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import lombok.Builder;
import lombok.Value;
import overcooked.core.GlobalState;

/**
 * The data about the execution of a state machine.
 */
@SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
    justification = "this is just for internal use, making it immutable is over engineering")
@Builder
@Value
public class StateMachineExecutionData {
  Set<Transition> transitions;
  Set<GlobalState> validationFailingGlobalStates;
  GlobalState initialState;
}
