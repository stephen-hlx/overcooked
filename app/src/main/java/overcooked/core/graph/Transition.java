package overcooked.core.graph;

import lombok.Builder;
import lombok.Value;
import overcooked.core.GlobalState;

@Builder
@Value
public class Transition {
    GlobalState from;
    String actionPerformerId;
    String methodName;
    String actionReceiverId;
    GlobalState to;
}
