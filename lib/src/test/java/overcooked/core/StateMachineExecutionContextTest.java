package overcooked.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.LocalState;

class StateMachineExecutionContextTest {
  // TODO - split the two cases
  @Test
  void registerOrGetDuplicate_works() {
    final String actor1 = "actor1";
    final int stateData1 = 1;
    GlobalState initialState = new GlobalState(ImmutableMap.of(
        new ActorId("actor0"),
        new TestLocalState(0)));
    GlobalState state1 = new GlobalState(ImmutableMap.of(
        new ActorId(actor1),
        new TestLocalState(stateData1)));
    StateMachineExecutionContext context = new StateMachineExecutionContext(initialState);

    GlobalState actual = context.registerOrGetDuplicate(state1);
    assertThat(actual).isEqualTo(state1);
    assertThat(actual).isSameAs(state1);
    assertThat(actual.getId()).isEqualTo(state1.getId());

    GlobalState state1Duplicate = new GlobalState(ImmutableMap.of(
        new ActorId(actor1),
        new TestLocalState(stateData1)));
    assertThat(state1Duplicate).isEqualTo(state1);
    assertThat(state1Duplicate).isNotSameAs(state1);
    assertThat(state1Duplicate.getId()).isNotEqualTo(state1.getId());

    actual = context.registerOrGetDuplicate(state1Duplicate);
    assertThat(actual).isEqualTo(state1);
    assertThat(actual).isSameAs(state1);
    assertThat(actual.getId()).isEqualTo(state1.getId());
  }

  @Test
  void registerOrGetDuplicate_works_for_initial_state() {
    final String actor0 = "actor0";
    final int stateData = 0;
    GlobalState initialState = new GlobalState(ImmutableMap.of(
        new ActorId(actor0),
        new TestLocalState(stateData)));
    StateMachineExecutionContext context = new StateMachineExecutionContext(initialState);

    GlobalState initialStateDuplicate = new GlobalState(ImmutableMap.of(
        new ActorId(actor0),
        new TestLocalState(stateData)));
    assertThat(initialStateDuplicate).isEqualTo(initialState);
    assertThat(initialStateDuplicate).isNotSameAs(initialState);
    assertThat(initialStateDuplicate.getId()).isNotEqualTo(initialState.getId());

    GlobalState actual = context.registerOrGetDuplicate(initialStateDuplicate);
    assertThat(actual).isEqualTo(initialState);
    assertThat(actual).isSameAs(initialState);
    assertThat(actual.getId()).isEqualTo(initialState.getId());
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  private static class TestLocalState extends LocalState {
    int data;
  }
}