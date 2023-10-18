package overcooked.core.tracing;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Tracer {
    private final Set<GlobalStateNode> nodes;

    public Tracer() {
        nodes = new HashSet<>();
    }

    public void capture(Transition transition) {

    }
}
