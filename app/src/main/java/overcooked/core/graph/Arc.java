package overcooked.core.graph;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Arc {
    String actionPerformerId;
    String methodName;
    String actionReceiverId;
}
