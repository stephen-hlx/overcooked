package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorDefinition;
import overcooked.sample.diehard.model.Jar3;

class TransitiveActionTypeTest {
  @Test
  void works() {
    ActorDefinition actorDefinition = ActorDefinition.builder()
        .id("doesn't matter")
        .type(Jar3.class)
        .build();
    ActionType someType = new TransitiveActionType(actorDefinition);
    assertThat(someType.isTransitive()).isTrue();
    assertThat(someType.getActionReceiverDefinition())
        .isEqualTo(actorDefinition);
  }

}