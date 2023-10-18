package overcooked.core.tracing;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class Node<Type, Action> {
    @EqualsAndHashCode.Include
    private final Type id;

    private final Map<Action, Set<Type>> outgoingArcs;
    boolean failureNode = false;

    public void setFailureNode() {
        this.failureNode = true;
    }
}
