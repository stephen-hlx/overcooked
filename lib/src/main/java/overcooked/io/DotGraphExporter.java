package overcooked.io;

import java.util.Set;
import overcooked.analysis.Transition;

/**
 * Builds a DOT string that can be used to create a DOT file.
 */
public interface DotGraphExporter {
  /**
   * Builds a DOT string using the transitions provided.
   *
   * @param transitions the {@link Transition}s of a state machine
   * @return the string representing the DOT graph
   */
  String export(Set<Transition> transitions);
}
