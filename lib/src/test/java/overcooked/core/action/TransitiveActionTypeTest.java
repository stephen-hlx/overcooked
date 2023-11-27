package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import overcooked.core.actor.Actor;

class TransitiveActionTypeTest {
  @Test
  void works() {
    Actor actor = Actor.builder()
        .id("doesn't matter")
        .build();
    ActionType someType = new TransitiveActionType(actor);
    assertThat(someType.isTransitive()).isTrue();
    assertThat(someType.getActionReceiverDefinition())
        .isEqualTo(actor);
  }

}