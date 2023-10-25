package overcooked.core.action;

/**
 * An {@link ActionType} that is intransitive.
 */
public class IntransitiveActionType implements ActionType {
  @Override
  public boolean isTransitive() {
    return false;
  }
}
