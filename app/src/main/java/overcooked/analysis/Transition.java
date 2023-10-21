package overcooked.analysis;

import lombok.Builder;
import lombok.Value;
import overcooked.core.GlobalState;

/**
 * Represents a transition in the state machine.
 * This is used to construct the graph that describes the execution of the state machine.
 */
@Builder
@Value
public class Transition {
  GlobalState from;
  String actionPerformerId;
  String methodName;
  String actionReceiverId;
  GlobalState to;
}
