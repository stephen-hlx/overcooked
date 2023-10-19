package overcooked.core.tracing;

import com.google.common.collect.ImmutableSet;
import overcooked.core.GlobalState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphBuilder {
    private final Set<Transition> transitions = new HashSet<>();

    Set<GlobalStateNode> getNodes() {
        Map<GlobalState, GlobalStateNode> nodes = new HashMap<>();
        transitions.forEach(transition -> {
            GlobalStateNode from = nodes.computeIfAbsent(transition.getFrom(), GlobalStateNode::new);
            GlobalStateNode to = nodes.computeIfAbsent(transition.getTo(), GlobalStateNode::new);
            from.addArc(Arc.builder()
                .actionPerformerId(transition.getActionPerformerId())
                .methodName(transition.getMethodName())
                .actionReceiverId(transition.getActionReceiverId())
                .build(), to);
        });
        return ImmutableSet.copyOf(nodes.values());
    }

    public void capture(Transition transition) {
        this.transitions.add(transition);
    }
}
