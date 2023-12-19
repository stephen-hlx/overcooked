package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorId;

class TransitiveActionTypeTest {
  @Test
  void works() {
    ActorId actorId = ActorId.builder()
        .id("doesn't matter")
        .build();
    ActionType someType = new TransitiveActionType(actorId);
    assertThat(someType.isTransitive()).isTrue();
    assertThat(someType.getActionReceiverId())
        .isEqualTo(actorId);
  }

}