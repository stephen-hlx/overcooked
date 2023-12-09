package overcooked.core.action;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Describes an action, with its type, the method name as well as the parameters.
 */
@Builder
@Getter(AccessLevel.PACKAGE)
@EqualsAndHashCode
@Value
public class ActionDefinition {
  ActionType actionType;

  String methodName;

  ParamValue paramValue;
}
