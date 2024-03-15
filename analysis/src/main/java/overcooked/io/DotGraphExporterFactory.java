package overcooked.io;

import com.google.common.collect.ImmutableMap;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import overcooked.analysis.JgraphtGraphBuilder;
import overcooked.analysis.Transition;
import overcooked.core.GlobalState;

/**
 * Factory of {@link DotGraphExporter}.
 */
public class DotGraphExporterFactory {
  /**
   * Creates a {@link DotGraphExporter} object.
   *
   * @return the {@link DotGraphExporter} object created
   */
  public static DotGraphExporter create() {
    DOTExporter<GlobalState, Transition> dotExporter = new DOTExporter<>();

    dotExporter.setVertexIdProvider(globalState -> "S_" + globalState.getId());

    dotExporter.setVertexAttributeProvider(globalState -> ImmutableMap.of(
        "label",
        DefaultAttribute.createAttribute(GlobalStatePrinter.print(globalState))
    ));

    dotExporter.setEdgeAttributeProvider(transition -> ImmutableMap.of(
        "label",
        DefaultAttribute.createAttribute(ArcPrinter.printArc(transition.getArc()))));

    return new JgraphtDotGraphExporter(new JgraphtGraphBuilder(), dotExporter);
  }
}
