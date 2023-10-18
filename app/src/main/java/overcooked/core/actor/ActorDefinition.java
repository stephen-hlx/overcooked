package overcooked.core.actor;

import lombok.Builder;
import lombok.Value;

// TODO equals should use id only
@Value
@Builder
public class ActorDefinition {
    String id;
    Class<?> type;
    // TODO: is this really needed?
    Class<?> localStateType;
}
