package overcooked.core.action;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import overcooked.core.actor.ActorDefinition;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class TransitiveActionType implements ActionType {
    private final ActorDefinition actionReceiverDefinition;
    @Override
    public boolean isTransitive() {
        return true;
    }
}
