package overcooked.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorEnvState;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;
import overcooked.core.actor.LocalState;

class StateMergerTest {
  private static final ActorId ACTOR_ID_0 = new ActorId("0");
  private static final ActorId ACTOR_ID_1 = new ActorId("1");

  @Test
  @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH")
  void updates_to_old_global_state_does_not_affect_new_global_state() {
    Actor0ActorState actor0State0 = new Actor0ActorState(ImmutableMap.of("actor0", "state0"));
    LocalState actor0LocalState = LocalState.builder()
        .actorState(actor0State0)
        .actorEnvState(new ActorEnvState())
        .build();
    Map<String, String> actor1State0internal = new HashMap<>();
    actor1State0internal.put("actor1", "state0");
    Actor1ActorState actor1State0 = new Actor1ActorState(actor1State0internal);
    LocalState actor1LocalState = LocalState.builder()
        .actorState(actor1State0)
        .actorEnvState(new ActorEnvState())
        .build();
    GlobalState oldGlobalState = new GlobalState(ImmutableMap.of(
        ACTOR_ID_0, actor0LocalState,
        ACTOR_ID_1, actor1LocalState
    ));

    Map<ActorId, RuntimeException> actor0Rejections = new HashMap<>();
    actor0Rejections.put(ACTOR_ID_1, new RuntimeException());
    ActorEnvState actorEnvState0 = new ActorEnvState(actor0Rejections);

    GlobalState newGlobalState =
        new StateMerger().merge(oldGlobalState, ImmutableMap.of(ACTOR_ID_0,
            LocalState.builder()
                .actorState(new Actor0ActorState(ImmutableMap.of("actor0", "state1")))
                .actorEnvState(actorEnvState0)
                .build()));

    GlobalState expected = new GlobalState(ImmutableMap.of(
        ACTOR_ID_0, LocalState.builder()
            .actorEnvState(new ActorEnvState(actor0Rejections))
            .actorState(new Actor0ActorState(ImmutableMap.of("actor0", "state1")))
            .build(),
        ACTOR_ID_1, LocalState.builder()
            .actorState(new Actor1ActorState(ImmutableMap.of("actor1", "state0")))
            .build()
    ));
    assertThat(newGlobalState).isEqualTo(expected);

    // change the old global state
    // especially actor 1 as its state did not change during the merge
    ((Actor1ActorState) (oldGlobalState.getCopyOfLocalStates().get(ACTOR_ID_1).getActorState()))
        .getData()
        .put("actor1", "state1");

    // TODO: can we verify this using `!=`?
    assertThat(newGlobalState)
        .as("changes made to the old global state should not affect the new global state")
        .isEqualTo(expected);
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