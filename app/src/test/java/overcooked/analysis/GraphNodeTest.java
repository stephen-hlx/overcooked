package overcooked.analysis;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import overcooked.analysis.GraphNode;

import static org.assertj.core.api.Assertions.assertThat;

class GraphNodeTest {
    // Comparison could end up in infinite recursion due to possible loops in the graph
    @Test
    void only_id_is_used_for_equals() {
        assertThat(node(1, "arc1")).isEqualTo(node(1, "arc2"));
        // usingFieldByFieldElementComparator is somehow only available to comparing collections
        assertThat(ImmutableSet.of(node(1, "arc1")))
            .usingFieldByFieldElementComparator()
            .isNotEqualTo(ImmutableSet.of(node(1, "arc2")));
        assertThat(node(1, "arc1")).isNotEqualTo(node(2, "arc1"));
    }

    private static GraphNode<Integer, String> node(int id, String arcName) {
        GraphNode<Integer, String> graphNode = new GraphNode<>(id);
        graphNode.addArc(arcName, new GraphNode<>(0));
        return graphNode;
    }

}