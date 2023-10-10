package overcooked.core.action;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Builder
@Getter
@Value
public class ActionTemplate {
    ActionType actionType;

    String methodName;

    @Builder.Default
    List<Param> parameters = Collections.emptyList();
}
