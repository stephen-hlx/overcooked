package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IntransitiveActionTypeTest {
  @Test
  void isTransitive_returns_false() {
    assertThat(new IntransitiveActionType().isTransitive()).isFalse();
  }

}