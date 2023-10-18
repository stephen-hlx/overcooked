package overcooked.core.action;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Builder
@Getter
@Value
public class ActionTemplate {
    ActionType actionType;

    String methodName;

    @Builder.Default
    ImmutableList<Param> parameters = ImmutableList.of();
}
