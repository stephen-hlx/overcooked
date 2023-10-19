package overcooked.core.visual;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import overcooked.core.GlobalState;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalStatePrinterTest {
    @Test
    void works() {
        String actor1Id = "actor1";
        String actor2Id = "actor2";

        ActorDefinition actor1 = ActorDefinition.builder().id(actor1Id).type(Integer.class).build();
        ActorDefinition actor2 = ActorDefinition.builder().id(actor2Id).type(Boolean.class).build();

        LocalState actor1LocalState = new TestLocalState(1, 0);
        LocalState actor2LocalState = new TestLocalState(2, 0);

        assertThat(new GlobalStatePrinter().print(new GlobalState(
            ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, actor1LocalState)
                .put(actor2, actor2LocalState)
                .build())))
            .isEqualTo("actor2(a=2,b=0), actor1(a=1,b=0)");
    }
}