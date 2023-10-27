package overcooked.visual;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.jgrapht.nio.dot.DOTExporter;
import overcooked.analysis.JgraphtGraphBuilder;
import overcooked.analysis.Transition;
import overcooked.core.GlobalState;

/**
 * A JGraphT based implementation of {@link DotGraphExporter}.
 */
@RequiredArgsConstructor
@SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
    justification = "this is just for internal use, making it immutable is over engineering")
public class JgraphtDotGraphExporter implements DotGraphExporter {
  private final JgraphtGraphBuilder jgraphtGraphBuilder;
  private final DOTExporter<GlobalState, Transition> dotExporter;

  @Override
  public String export(Set<Transition> transitions) {
    try (StringWriter sw = new StringWriter()) {
      dotExporter.exportGraph(jgraphtGraphBuilder.build(transitions), sw);
      return sw.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
