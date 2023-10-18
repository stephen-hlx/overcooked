package overcooked.core.tracing;

import overcooked.core.GlobalState;

import java.util.HashMap;

public class GlobalStateNode extends Node<GlobalState, Arc> {
    public GlobalStateNode(GlobalState id) {
        super(id, new HashMap<>());
    }
}
