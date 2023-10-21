package overcooked.visual;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import overcooked.analysis.Transition;

/**
 * Builds a DOT string that can be used to create a DOT file.
 */
@RequiredArgsConstructor
public class DotGraphBuilder {
  private final TransitionPrinter transitionPrinter;

  /**
   * Builds a DOT string using the transitions provided.
   *
   * @param transitions the {@link Transition}s of a state machine
   * @return a DOT string
   */
  public String build(Set<Transition> transitions) {
    String content = transitions.stream()
        .map(transition -> buildLine(transitionPrinter.print(transition)))
        .collect(Collectors.joining("\n"));
    return String.format("digraph G {%n%s%n}", content);
  }

  private static String buildLine(String s) {
    return "\t" + s + ";";
  }
}
