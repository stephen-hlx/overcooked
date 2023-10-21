package overcooked.analysis;

import lombok.Getter;
import lombok.ToString;
import overcooked.core.GlobalState;

@Getter
public class GlobalStateNode extends GraphNode<GlobalState, Arc> {
    @ToString.Include
    private final boolean failureNode;

    public GlobalStateNode(boolean isFailure, GlobalState id) {
        super(id);
        this.failureNode = isFailure;
    }
}
