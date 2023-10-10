package overcooked.core.actor;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ActorDefinition {
    String id;
    Class<?> type;
    // TODO: is this really needed?
    Class<?> localStateType;
}
