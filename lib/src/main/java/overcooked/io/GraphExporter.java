package overcooked.io;

import java.util.Set;
import overcooked.analysis.Transition;

/**
 * Exports the graph as a string.
 */
public interface GraphExporter {
  /**
   * Generates a string representation of the graph using the transitions provided.
   *
   * @param transitions the {@link Transition}s of a state machine
   * @return the string representing the graph
   */
  String export(Set<Transition> transitions);
}
