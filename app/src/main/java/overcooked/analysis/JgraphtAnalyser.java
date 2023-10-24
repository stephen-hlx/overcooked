package overcooked.analysis;

import java.util.HashSet;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import overcooked.core.GlobalState;

/**
 * A JGraphT based {@link Analyser} implementation.
 */
public class JgraphtAnalyser implements Analyser {
  @Override
  public Set<Transition> findShortestPathToFailureState(GlobalState initialState,
                                                        GlobalState failureState,
                                                        Set<Transition> transitions) {
    Graph<GlobalState, Transition> graph = new DefaultDirectedGraph<>(Transition.class);
    transitions.forEach(transition -> {
      GlobalState from = transition.getFrom();
      graph.addVertex(from);
      GlobalState to = transition.getTo();
      graph.addVertex(to);
      graph.addEdge(from, to, transition);
    });

    DijkstraShortestPath<GlobalState, Transition> dijkstraAlg =
        new DijkstraShortestPath<>(graph);
    ShortestPathAlgorithm.SingleSourcePaths<GlobalState, Transition> paths =
        dijkstraAlg.getPaths(initialState);
    return new HashSet<>(paths.getPath(failureState).getEdgeList());
  }
}
