package overcooked.core.action;

import lombok.EqualsAndHashCode;
import overcooked.core.actor.ActorDefinition;

public interface ActionType {
    boolean isTransitive();

    // TODO: review this default method, should it be here?
    default ActorDefinition getActionReceiverDefinition() {
        throw new UnsupportedOperationException();
    }
}
