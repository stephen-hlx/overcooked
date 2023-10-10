package overcooked.core.action;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Builder
@Getter(AccessLevel.PACKAGE)
@EqualsAndHashCode
@Value
public class ActionDefinition {
    ActionType actionType;

    String methodName;

    @Builder.Default
    List<ParamValue> parameters = Collections.emptyList();
}
