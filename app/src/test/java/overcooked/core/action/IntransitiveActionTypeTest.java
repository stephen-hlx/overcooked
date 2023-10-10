package overcooked.core.action;

import org.junit.jupiter.api.Test;
import overcooked.core.action.IntransitiveActionType;

import static org.assertj.core.api.Assertions.assertThat;

class IntransitiveActionTypeTest {
    @Test
    void isTransitive_returns_false() {
        assertThat(new IntransitiveActionType().isTransitive()).isFalse();
    }

}