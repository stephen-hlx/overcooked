package overcooked.core.action;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

/**
 * The template of an action to be performed by an actor in the system to be model verified.
 */
@Builder
@Getter
@Value
public class ActionTemplate {
  ActionType actionType;

  String methodName;

  @Builder.Default
  ImmutableList<Param> parameters = ImmutableList.of();
}
