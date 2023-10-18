package overcooked.core.tracing;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

class NodeTest {
    @Test
    void only_id_is_used_for_equals() {
        assertThat(node(1, true)).isEqualTo(node(1, false));
        assertThat(node(1, true)).isNotEqualTo(node(2, true));
    }

    private static Node<Integer, Boolean> node(int id, boolean action) {
        return new Node<>(id, ImmutableMap.of(action, emptySet()));
    }

}