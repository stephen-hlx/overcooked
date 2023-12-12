package overcooked.analysis;

import lombok.Builder;
import lombok.Value;

/**
 * An object representing an arc from one {@link overcooked.core.GlobalState} to another.
 */
@Builder
@Value
public class Arc {
  String actionPerformerId;
  String label;
  String actionReceiverId;
}
