package overcooked.analysis;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import overcooked.core.GlobalState;

/**
 * A JGraphT based {@link Analyser} implementation.
 */
public class JgraphtAnalyser implements Analyser {
  private final JgraphtGraphBuilder graphBuilder;

  public JgraphtAnalyser() {
    this(new JgraphtGraphBuilder());
  }

  JgraphtAnalyser(JgraphtGraphBuilder graphBuilder) {
    this.graphBuilder = graphBuilder;
  }

  @Override
  public Set<Transition> findShortestPathToFailureState(GlobalState initialState,
                                                         GlobalState failureState,
                                                         Set<Transition> transitions) {
    DijkstraShortestPath<GlobalState, Transition> dijkstraAlg =
        new DijkstraShortestPath<>(graphBuilder.build(transitions));
    ShortestPathAlgorithm.SingleSourcePaths<GlobalState, Transition> paths =
        dijkstraAlg.getPaths(initialState);
    return ImmutableSet.copyOf(paths.getPath(failureState).getEdgeList());
  }
}
