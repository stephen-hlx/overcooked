package overcooked.core;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

import java.util.Map;

@Getter
@EqualsAndHashCode
public class GlobalState {
    // <actor, localState>
    private final ImmutableMap<ActorDefinition, LocalState> localStates;

    public GlobalState(Map<ActorDefinition, LocalState> localStates) {
        this.localStates = ImmutableMap.copyOf(localStates);
    }
}
