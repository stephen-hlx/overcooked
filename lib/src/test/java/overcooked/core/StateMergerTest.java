package overcooked.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

class StateMergerTest {
  private static final ActorDefinition ACTOR_0 = ActorDefinition.builder().id("0").build();
  private static final ActorDefinition ACTOR_1 = ActorDefinition.builder().id("1").build();

  @Test
  @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH")
  void updates_to_old_global_state_does_not_affect_new_global_state() {
    Actor0LocalState actor0LocalState0 = new Actor0LocalState(ImmutableMap.of("actor0", "state0"));
    Map<String, String> actor1LocalState0internal = new HashMap<>();
    actor1LocalState0internal.put("actor1", "state0");
    Actor1LocalState actor1LocalState0 = new Actor1LocalState(actor1LocalState0internal);
    GlobalState oldGlobalState = new GlobalState(ImmutableMap.of(
        ACTOR_0, actor0LocalState0,
        ACTOR_1, actor1LocalState0
    ));

    GlobalState newGlobalState =
        new StateMerger()
            .merge(oldGlobalState, ImmutableMap.of(ACTOR_0,
                new Actor0LocalState(ImmutableMap.of("actor0", "state1"))));

    assertThat(newGlobalState).isEqualTo(new GlobalState(ImmutableMap.of(
        ACTOR_0, new Actor0LocalState(ImmutableMap.of("actor0", "state1")),
        ACTOR_1, new Actor1LocalState(ImmutableMap.of("actor1", "state0"))
    )));

    // change the old global state
    ((Actor1LocalState) (oldGlobalState.getCopyOfLocalStates().get(ACTOR_1))).getData()
        .put("actor1", "state1");

    assertThat(newGlobalState)
        .as("changes made to the old global state should not affect the new global state")
        .isEqualTo(new GlobalState(ImmutableMap.of(
            ACTOR_0, new Actor0LocalState(ImmutableMap.of("actor0", "state1")),
            ACTOR_1, new Actor1LocalState(ImmutableMap.of("actor1", "state0"))
        )));
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  static class Actor0LocalState extends LocalState {
    Map<String, String> data;
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  static class Actor1LocalState extends LocalState {
    Map<String, String> data;
  }
}