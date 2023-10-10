package overcooked.core;

import lombok.Value;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

import java.util.Map;

@Value
public class GlobalState {
    // <actor, localState>
    Map<ActorDefinition, LocalState> localStates;
}
