package overcooked.visual;

import lombok.Value;
import overcooked.core.actor.LocalState;

@Value
class TestLocalState implements LocalState {
    int a;
    int b;

    public String toString() {
        return String.format("a=%d,b=%d", a, b);
    }
}
