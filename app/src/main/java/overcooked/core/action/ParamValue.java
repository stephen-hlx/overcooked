package overcooked.core.action;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Represents a value, including the type and the value itself.
 */
@RequiredArgsConstructor
@Getter(AccessLevel.PACKAGE)
@Value
class ParamValue implements Param {
  Class<?> clazz;
  Object value;

  @Override
  public boolean isTemplate() {
    return false;
  }
}
