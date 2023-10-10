package overcooked.core.action;

import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorDefinition;
import overcooked.sample.diehard.model.Jar3;
import overcooked.sample.diehard.modelverifier.Jar3State;

import static org.assertj.core.api.Assertions.assertThat;

class TransitiveActionTypeTest {
    @Test
    void works() {
        ActorDefinition actorDefinition = ActorDefinition.builder()
            .id("doesn't matter")
            .type(Jar3.class)
            .localStateType(Jar3State.class)
            .build();
        ActionType someType = new TransitiveActionType(actorDefinition);
        assertThat(someType.isTransitive()).isTrue();
        assertThat(someType.getActionReceiverDefinition())
            .isEqualTo(actorDefinition);
    }

}