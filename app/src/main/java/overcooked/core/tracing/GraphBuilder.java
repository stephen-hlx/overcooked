package overcooked.core.tracing;

import com.google.common.collect.ImmutableSet;
import overcooked.core.GlobalState;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GraphBuilder {
    private final Map<GlobalState, GlobalStateNode> nodes = new HashMap<>();

    Set<GlobalStateNode> getNodes() {
        return ImmutableSet.copyOf(this.nodes.values());
    }

    public void capture(Transition transition) {
        GlobalStateNode from = nodes.computeIfAbsent(transition.getFrom(), GlobalStateNode::new);
        GlobalStateNode to = nodes.computeIfAbsent(transition.getTo(), GlobalStateNode::new);
        from.addArc(Arc.builder()
                .actionPerformerId(transition.getActionPerformerId())
                .methodName(transition.getMethodName())
                .actionReceiverId(transition.getActionReceiverId())
            .build(), to);
    }
}
