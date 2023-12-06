package overcooked.analysis;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

/**
 * The interface of a transition filter that can be used to keep only the transitions
 * we want to export.
 */
@RequiredArgsConstructor
public enum TransitionFilter implements Predicate<Transition> {
  NON_SELF_LOOP(transition -> !transition.getFrom().equals(transition.getTo())),
  EXCEPTION_FREE(transition -> transition.getActionResult().isSuccess());

  private final Predicate<Transition> transitionFilter;

  @Override
  public boolean test(Transition transition) {
    return this.transitionFilter.test(transition);
  }
}
