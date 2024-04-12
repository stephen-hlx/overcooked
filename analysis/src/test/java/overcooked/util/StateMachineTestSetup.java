package overcooked.util;

import com.google.common.collect.ImmutableMap;
import overcooked.analysis.Arc;
import overcooked.analysis.Transition;
import overcooked.core.GlobalState;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;

/**
 * This class provides objects for use in testing.
 * They form a state machine like the one illustrated below:
 * <pre>
 *          ┌─┐     ┌───────┐
 *          │ │     │       │
 * ┌───┐   ┌┴─▼┐   ┌┴──┐   ┌▼──┐   ┌───┐
 * │ 1 │◄──┤ 0 ├──►│ 2 ├──►│ 3 ├──►│ 4 │
 * └───┘   └─┬─┘   └───┘   └─▲─┘   └───┘
 *           │               │
 *           └───────────────┘
 * </pre>
 */
public class StateMachineTestSetup {
  public static final String ACTOR_1_METHOD = "actor1method1";
  public static final String ACTOR_2_METHOD = "actor2method1";
  public static final String ACTOR_2_METHOD_2 = "actor2method2";
  public static final String ACTOR_3_METHOD = "actor3method1";
  public static final String ACTOR_3_METHOD_2 = "actor3method2";
  public static final String ACTOR_4_METHOD = "actor4method1";
  public static final ActorId ACTOR_ID_1 = new ActorId("actor1");
  public static final ActorId ACTOR_ID_2 = new ActorId("actor2");
  public static final ActorId ACTOR_ID_3 = new ActorId("actor3");
  public static final ActorId ACTOR_ID_4 = new ActorId("actor4");
  public static final ActorState ACTOR_1_STATE = new TestActorState(1, 0);
  public static final ActorState ACTOR_2_STATE = new TestActorState(2, 0);
  public static final ActorState ACTOR_3_STATE = new TestActorState(3, 0);
  public static final ActorState ACTOR_4_STATE = new TestActorState(4, 0);
  public static final ActorState NEW_ACTOR_1_STATE = new TestActorState(1, 1);
  public static final ActorState NEW_ACTOR_2_STATE = new TestActorState(2, 1);
  public static final ActorState NEW_ACTOR_3_STATE = new TestActorState(3, 1);

  public static final GlobalState GLOBAL_STATE_0 = new GlobalState(
      ImmutableMap.<ActorId, ActorState>builder()
          .put(ACTOR_ID_1, ACTOR_1_STATE)
          .put(ACTOR_ID_2, ACTOR_2_STATE)
          .put(ACTOR_ID_3, ACTOR_3_STATE)
          .put(ACTOR_ID_4, ACTOR_4_STATE)
          .build());

  public static final GlobalState GLOBAL_STATE_1 = new GlobalState(
      ImmutableMap.<ActorId, ActorState>builder()
          .put(ACTOR_ID_1, NEW_ACTOR_1_STATE)
          .put(ACTOR_ID_2, ACTOR_2_STATE)
          .put(ACTOR_ID_3, ACTOR_3_STATE)
          .put(ACTOR_ID_4, ACTOR_4_STATE)
          .build());

  public static final GlobalState GLOBAL_STATE_2 = new GlobalState(
      ImmutableMap.<ActorId, ActorState>builder()
          .put(ACTOR_ID_1, ACTOR_1_STATE)
          .put(ACTOR_ID_2, NEW_ACTOR_2_STATE)
          .put(ACTOR_ID_3, NEW_ACTOR_3_STATE)
          .put(ACTOR_ID_4, ACTOR_4_STATE)
          .build());

  public static final GlobalState GLOBAL_STATE_3 = new GlobalState(
      ImmutableMap.<ActorId, ActorState>builder()
          .put(ACTOR_ID_1, ACTOR_1_STATE)
          .put(ACTOR_ID_2, NEW_ACTOR_2_STATE)
          .put(ACTOR_ID_3, ACTOR_3_STATE)
          .put(ACTOR_ID_4, ACTOR_4_STATE)
          .build());

  public static final GlobalState GLOBAL_STATE_4 = new GlobalState(
      ImmutableMap.<ActorId, ActorState>builder()
          .put(ACTOR_ID_1, NEW_ACTOR_1_STATE)
          .put(ACTOR_ID_2, NEW_ACTOR_2_STATE)
          .put(ACTOR_ID_3, ACTOR_3_STATE)
          .put(ACTOR_ID_4, ACTOR_4_STATE)
          .build());

  public static final Arc ARC_0_0 = Arc.builder()
      .actionPerformerId(ACTOR_ID_4)
      .label(ACTOR_4_METHOD)
      .build();
  public static final Transition TRANSITION_0_0 = Transition.builder()
        .from(GLOBAL_STATE_0)
        .arc(ARC_0_0)
        .to(GLOBAL_STATE_0)
        .build();

  public static final Arc ARC_0_1 = Arc.builder()
      .actionPerformerId(ACTOR_ID_1)
      .label(ACTOR_1_METHOD)
      .build();
  public static final Transition TRANSITION_0_1 = Transition.builder()
      .from(GLOBAL_STATE_0)
      .arc(ARC_0_1)
      .to(GLOBAL_STATE_1)
      .build();

  public static final Arc ARC_0_2 = Arc.builder()
      .actionPerformerId(ACTOR_ID_1)
      .label(ACTOR_2_METHOD)
      .actionReceiverId(ACTOR_ID_3)
      .build();
  public static final Transition TRANSITION_0_2 = Transition.builder()
      .from(GLOBAL_STATE_0)
      .arc(ARC_0_2)
      .to(GLOBAL_STATE_2)
      .build();

  public static final Arc ARC_2_3_I = Arc.builder()
      .actionPerformerId(ACTOR_ID_3)
      .label(ACTOR_3_METHOD)
      .build();
  public static final Transition TRANSITION_2_3_I = Transition.builder()
      .from(GLOBAL_STATE_2)
      .arc(ARC_2_3_I)
      .to(GLOBAL_STATE_3)
      .build();

  public static final Arc ARC_2_3_II = Arc.builder()
      .actionPerformerId(ACTOR_ID_3)
      .label(ACTOR_3_METHOD_2)
      .build();
  public static final Transition TRANSITION_2_3_II = Transition.builder()
      .from(GLOBAL_STATE_2)
      .arc(ARC_2_3_II)
      .to(GLOBAL_STATE_3)
      .build();

  public static final Arc ARC_3_4 = Arc.builder()
      .actionPerformerId(ACTOR_ID_1)
      .label(ACTOR_1_METHOD)
      .build();
  public static final Transition TRANSITION_3_4 = Transition.builder()
      .from(GLOBAL_STATE_3)
      .arc(ARC_3_4)
      .to(GLOBAL_STATE_4)
      .build();

  public static final Arc ARC_0_3 = Arc.builder()
      .actionPerformerId(ACTOR_ID_2)
      .label(ACTOR_2_METHOD_2)
      .build();
  public static final Transition TRANSITION_0_3 = Transition.builder()
      .from(GLOBAL_STATE_0)
      .arc(ARC_0_3)
      .to(GLOBAL_STATE_3)
      .build();
}
