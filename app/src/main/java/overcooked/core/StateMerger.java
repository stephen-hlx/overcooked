package overcooked.core;

import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

import java.util.HashMap;
import java.util.Map;

// TODO: cloning the entire state is a must
public class StateMerger {
    public GlobalState merge(GlobalState globalState, Map<ActorDefinition, LocalState> localStates) {
        HashMap<ActorDefinition, LocalState> shallowCopy = new HashMap<>(globalState.getLocalStates());
        shallowCopy.putAll(localStates);
        return new GlobalState(shallowCopy);
    }
}
