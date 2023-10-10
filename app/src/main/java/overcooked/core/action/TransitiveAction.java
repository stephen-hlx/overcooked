package overcooked.core.action;

import lombok.Builder;
import lombok.Value;
import overcooked.core.action.ActionTemplate;

@Builder
@Value
public class TransitiveAction {
    Object actionPerformer;
    Class<?> actionReceiverType;
    Object actionReceiver;
    ActionTemplate actionTemplate;
}
