package overcooked.analysis;

import lombok.Builder;
import lombok.Value;
import overcooked.core.GlobalState;
import overcooked.core.action.ActionResult;

/**
 * Represents a transition in the state machine.
 * This is used to construct the graph that describes the execution of the state machine.
 */
@Builder
@Value
public class Transition {
  GlobalState from;
  Arc arc;
  GlobalState to;
  ActionResult actionResult;
}
