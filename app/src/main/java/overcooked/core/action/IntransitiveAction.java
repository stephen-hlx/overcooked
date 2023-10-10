package overcooked.core.action;

import lombok.Builder;
import lombok.Value;
import overcooked.core.action.ActionTemplate;

@Builder
@Value
public class IntransitiveAction {
    Object actor;
    ActionTemplate actionTemplate;
}
