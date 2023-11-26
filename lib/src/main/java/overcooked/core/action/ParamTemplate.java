package overcooked.core.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * A template of a parameter, specifying on the type of the parameter, not the value.
 *
 * @param <T> the type of the parameter
 */
@RequiredArgsConstructor
@Getter
@Value
public class ParamTemplate<T> implements Param {
  Class<T> type;

  @Override
  public boolean isTemplate() {
    return true;
  }
}
