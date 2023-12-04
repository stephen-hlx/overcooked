package overcooked.core.action;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * A structure that represents the execution failures, if there is any.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
    justification = "this is merely an internal structure")
public class ActionResult {
  Throwable throwable;

  public static ActionResult failure(Throwable throwable) {
    return new ActionResult(throwable);
  }

  public static ActionResult success() {
    return new ActionResult(null);
  }

  public boolean isSuccess() {
    return this.throwable == null;
  }
}
