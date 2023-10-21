package overcooked.visual;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import overcooked.analysis.Transition;

/**
 * This class can be made package private (TODO).
 */
@RequiredArgsConstructor
public class TransitionPrinter {
  private final GlobalStatePrinter globalStatePrinter;

  String print(Transition transition) {
    return String.format("%s -> %s [label=%s]",
        quoted(globalStatePrinter.print(transition.getFrom())),
        quoted(globalStatePrinter.print(transition.getTo())),
        quoted(String.format("%s.%s(%s)",
            transition.getActionPerformerId(),
            transition.getMethodName(),
            Strings.isNullOrEmpty(transition.getActionReceiverId()) ? "" :
                transition.getActionReceiverId())));
  }

  private static String quoted(String s) {
    return String.format("\"%s\"", s);
  }
}
