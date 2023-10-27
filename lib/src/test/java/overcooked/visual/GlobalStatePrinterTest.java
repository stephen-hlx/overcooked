package overcooked.visual;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import lombok.Value;
import org.junit.jupiter.api.Test;
import overcooked.core.GlobalState;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

class GlobalStatePrinterTest {
  @Test
  void works() {
    String actor1Id = "actor1";
    String actor2Id = "actor2";

    ActorDefinition actor1 = ActorDefinition.builder().id(actor1Id).type(Integer.class).build();
    ActorDefinition actor2 = ActorDefinition.builder().id(actor2Id).type(Boolean.class).build();

    LocalState actor1LocalState = new TestLocalState(1, 0);
    LocalState actor2LocalState = new TestLocalState(2, 0);

    assertThat(GlobalStatePrinter.print(new GlobalState(
        ImmutableMap.<ActorDefinition, LocalState>builder()
            .put(actor1, actor1LocalState)
            .put(actor2, actor2LocalState)
            .build())))
        .isEqualTo("actor2(f1=2,f2=0), actor1(f1=1,f2=0)");
  }

  @Value
  static class TestLocalState implements LocalState {
    int f1;
    int f2;

    public String toString() {
      return String.format("f1=%d,f2=%d", f1, f2);
    }
  }
}
