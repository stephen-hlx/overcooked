package overcooked.io;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.junit.jupiter.api.Test;
import overcooked.core.GlobalState;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;

class GlobalStatePrinterTest {
  @Test
  void works() {
    ActorId actorId1 = new ActorId("actor1");
    ActorId actorId2 = new ActorId("actor2");

    ActorState actor1ActorState = new TestActorState(1, 0);
    ActorState actor2ActorState = new TestActorState(2, 0);

    assertThat(GlobalStatePrinter.print(new GlobalState(
        ImmutableMap.<ActorId, ActorState>builder()
            .put(actorId1, actor1ActorState)
            .put(actorId2, actor2ActorState)
            .build())))
        .isEqualTo("actor1(f1=1,f2=0), actor2(f1=2,f2=0)");
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  static class TestActorState extends ActorState {
    int f1;
    int f2;

    public String toString() {
      return String.format("f1=%d,f2=%d", f1, f2);
    }
  }
}
