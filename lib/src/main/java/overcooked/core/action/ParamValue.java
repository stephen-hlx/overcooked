package overcooked.core.action;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Represents a value, including the type and the value itself.
 */
@RequiredArgsConstructor
@Getter
@Value
class ParamValue implements Param {
  @NonNull
  Class<?> type;
  @NonNull
  Object value;

  @Override
  public boolean isTemplate() {
    return false;
  }
}
