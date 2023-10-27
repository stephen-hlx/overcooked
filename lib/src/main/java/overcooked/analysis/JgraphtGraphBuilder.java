package overcooked.analysis;

import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import overcooked.core.GlobalState;

/**
 * Builder of a JGraphT graph.
 */
public class JgraphtGraphBuilder {

  /**
   * Builds a JGraphT graph using the transitions provided.
   *
   * @param transitions the set of {@link Transition}s used to build the graph
   * @return a {@link Graph} built based on the transitions provided
   */
  public Graph<GlobalState, Transition> build(Set<Transition> transitions) {
    Graph<GlobalState, Transition> graph = new DefaultDirectedGraph<>(Transition.class);

    transitions.forEach(transition -> {
      GlobalState from = transition.getFrom();
      graph.addVertex(from);
      GlobalState to = transition.getTo();
      graph.addVertex(to);
      graph.addEdge(from, to, transition);
    });

    return graph;
  }
}
