package overcooked.visual;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.junit.jupiter.api.Test;
import overcooked.core.GlobalState;
import overcooked.core.actor.Actor;
import overcooked.core.actor.LocalState;

class GlobalStatePrinterTest {
  @Test
  void works() {
    String actor1Id = "actor1";
    String actor2Id = "actor2";

    Actor actor1 = Actor.builder().id(actor1Id).build();
    Actor actor2 = Actor.builder().id(actor2Id).build();

    LocalState actor1LocalState = new TestLocalState(1, 0);
    LocalState actor2LocalState = new TestLocalState(2, 0);

    assertThat(GlobalStatePrinter.print(new GlobalState(
        ImmutableMap.<Actor, LocalState>builder()
            .put(actor1, actor1LocalState)
            .put(actor2, actor2LocalState)
            .build())))
        .isEqualTo("actor1(f1=1,f2=0), actor2(f1=2,f2=0)");
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  static class TestLocalState extends LocalState {
    int f1;
    int f2;

    public String toString() {
      return String.format("f1=%d,f2=%d", f1, f2);
    }
  }
}
