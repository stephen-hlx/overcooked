package overcooked.core.tracing;

import overcooked.core.GlobalState;

public class GlobalStateNode extends GraphNode<GlobalState, Arc> {
    public GlobalStateNode(GlobalState id) {
        super(id);
    }
}
