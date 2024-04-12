package overcooked.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;

class StateMergerTest {
  private static final ActorId ACTOR_ID_0 = new ActorId("0");
  private static final ActorId ACTOR_ID_1 = new ActorId("1");

  @Test
  @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH")
  void updates_to_old_global_state_does_not_affect_new_global_state() {
    Actor0ActorState actor0State0 = new Actor0ActorState(ImmutableMap.of("actor0", "state0"));
    Map<String, String> actor1State0internal = new HashMap<>();
    actor1State0internal.put("actor1", "state0");
    Actor1ActorState actor1State0 = new Actor1ActorState(actor1State0internal);
    GlobalState oldGlobalState = new GlobalState(ImmutableMap.of(
        ACTOR_ID_0, actor0State0,
        ACTOR_ID_1, actor1State0
    ));

    GlobalState newGlobalState =
        new StateMerger()
            .merge(oldGlobalState, ImmutableMap.of(ACTOR_ID_0,
                new Actor0ActorState(ImmutableMap.of("actor0", "state1"))));

    assertThat(newGlobalState).isEqualTo(new GlobalState(ImmutableMap.of(
        ACTOR_ID_0, new Actor0ActorState(ImmutableMap.of("actor0", "state1")),
        ACTOR_ID_1, new Actor1ActorState(ImmutableMap.of("actor1", "state0"))
    )));

    // change the old global state
    ((Actor1ActorState) (oldGlobalState.getCopyOfLocalStates().get(ACTOR_ID_1))).getData()
        .put("actor1", "state1");

    assertThat(newGlobalState)
        .as("changes made to the old global state should not affect the new global state")
        .isEqualTo(new GlobalState(ImmutableMap.of(
            ACTOR_ID_0, new Actor0ActorState(ImmutableMap.of("actor0", "state1")),
            ACTOR_ID_1, new Actor1ActorState(ImmutableMap.of("actor1", "state0"))
        )));
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  static class Actor0ActorState extends ActorState {
    Map<String, String> data;
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  static class Actor1ActorState extends ActorState {
    Map<String, String> data;
  }
}