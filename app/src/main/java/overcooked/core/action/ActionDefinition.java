package overcooked.core.action;

import com.google.common.collect.ImmutableList;
import lombok.*;

@Builder
@Getter(AccessLevel.PACKAGE)
@EqualsAndHashCode
@Value
public class ActionDefinition {
    ActionType actionType;

    String methodName;

    @Builder.Default
    ImmutableList<ParamValue> parameters = ImmutableList.of();
}
