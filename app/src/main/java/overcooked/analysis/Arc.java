package overcooked.analysis;

import lombok.Builder;
import lombok.Value;

/**
 * An object representing an arc from one {@link GlobalStateNode} to another.
 */
@Builder
@Value
public class Arc {
  String actionPerformerId;
  String methodName;
  String actionReceiverId;
}
