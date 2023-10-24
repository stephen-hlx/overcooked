package overcooked.analysis;

import com.google.common.collect.ImmutableMap;
import overcooked.core.GlobalState;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

/**
 * This class provides objects for use in testing.
 * They form a state machine like the one illustrated below:
 *          ┌─┐
 *          │ │
 * ┌───┐   ┌┴─▼┐   ┌───┐   ┌───┐   ┌───┐
 * │ 1 │◄──┤ 0 ├──►│ 2 ├──►│ 3 ├──►│ 4 │
 * └───┘   └─┬─┘   └───┘   └─▲─┘   └───┘
 *           │               │
 *           └───────────────┘
 */
class StateMachineTestSetup {
  public static final String ACTOR_1_ID = "actor1";
  public static final String ACTOR_2_ID = "actor2";
  public static final String ACTOR_3_ID = "actor3";
  public static final String ACTOR_4_ID = "actor4";
  public static final String ACTOR_1_METHOD = "actor1.method1";
  public static final String ACTOR_2_METHOD = "actor2.method1";
  public static final String ACTOR_2_METHOD_2 = "actor2.method2";
  public static final String ACTOR_3_METHOD = "actor3.method1";
  public static final String ACTOR_4_METHOD = "actor4.method1";
  public static final ActorDefinition ACTOR_1 = ActorDefinition.builder().id(ACTOR_1_ID).build();
  public static final ActorDefinition ACTOR_2 = ActorDefinition.builder().id(ACTOR_2_ID).build();
  public static final ActorDefinition ACTOR_3 = ActorDefinition.builder().id(ACTOR_3_ID).build();
  public static final ActorDefinition ACTOR_4 = ActorDefinition.builder().id(ACTOR_4_ID).build();
  public static final LocalState ACTOR_1_LOCAL_STATE = new TestLocalState(1, 0);
  public static final LocalState ACTOR_2_LOCAL_STATE = new TestLocalState(2, 0);
  public static final LocalState ACTOR_3_LOCAL_STATE = new TestLocalState(3, 0);
  public static final LocalState ACTOR_4_LOCAL_STATE = new TestLocalState(4, 0);
  public static final LocalState NEW_ACTOR_1_LOCAL_STATE = new TestLocalState(1, 1);
  public static final LocalState NEW_ACTOR_2_LOCAL_STATE = new TestLocalState(2, 1);
  public static final LocalState NEW_ACTOR_3_LOCAL_STATE = new TestLocalState(3, 1);

  public static final GlobalState GLOBAL_STATE_0 = new GlobalState(
      ImmutableMap.<ActorDefinition, LocalState>builder()
          .put(ACTOR_1, ACTOR_1_LOCAL_STATE)
          .put(ACTOR_2, ACTOR_2_LOCAL_STATE)
          .put(ACTOR_3, ACTOR_3_LOCAL_STATE)
          .put(ACTOR_4, ACTOR_4_LOCAL_STATE)
          .build());

  public static final GlobalState GLOBAL_STATE_1 = new GlobalState(
      ImmutableMap.<ActorDefinition, LocalState>builder()
          .put(ACTOR_1, NEW_ACTOR_1_LOCAL_STATE)
          .put(ACTOR_2, ACTOR_2_LOCAL_STATE)
          .put(ACTOR_3, ACTOR_3_LOCAL_STATE)
          .put(ACTOR_4, ACTOR_4_LOCAL_STATE)
          .build());

  public static final GlobalState GLOBAL_STATE_2 = new GlobalState(
      ImmutableMap.<ActorDefinition, LocalState>builder()
          .put(ACTOR_1, ACTOR_1_LOCAL_STATE)
          .put(ACTOR_2, NEW_ACTOR_2_LOCAL_STATE)
          .put(ACTOR_3, NEW_ACTOR_3_LOCAL_STATE)
          .put(ACTOR_4, ACTOR_4_LOCAL_STATE)
          .build());

  public static final GlobalState GLOBAL_STATE_3 = new GlobalState(
      ImmutableMap.<ActorDefinition, LocalState>builder()
          .put(ACTOR_1, ACTOR_1_LOCAL_STATE)
          .put(ACTOR_2, NEW_ACTOR_2_LOCAL_STATE)
          .put(ACTOR_3, ACTOR_3_LOCAL_STATE)
          .put(ACTOR_4, ACTOR_4_LOCAL_STATE)
          .build());

  public static final GlobalState GLOBAL_STATE_4 = new GlobalState(
      ImmutableMap.<ActorDefinition, LocalState>builder()
          .put(ACTOR_1, NEW_ACTOR_1_LOCAL_STATE)
          .put(ACTOR_2, NEW_ACTOR_2_LOCAL_STATE)
          .put(ACTOR_3, ACTOR_3_LOCAL_STATE)
          .put(ACTOR_4, ACTOR_4_LOCAL_STATE)
          .build());

  public static final Arc ARC_0_0 = Arc.builder()
      .actionPerformerId(ACTOR_4_ID)
      .methodName(ACTOR_4_METHOD)
      .build();
  public static final Transition TRANSITION_0_0 = Transition.builder()
        .from(GLOBAL_STATE_0)
        .arc(ARC_0_0)
        .to(GLOBAL_STATE_0)
        .build();

  public static final Arc ARC_0_1 = Arc.builder()
      .actionPerformerId(ACTOR_1_ID)
      .methodName(ACTOR_1_METHOD)
      .build();
  public static final Transition TRANSITION_0_1 = Transition.builder()
      .from(GLOBAL_STATE_0)
      .arc(ARC_0_1)
      .to(GLOBAL_STATE_1)
      .build();

  public static final Arc ARC_0_2 = Arc.builder()
      .actionPerformerId(ACTOR_2_ID)
      .methodName(ACTOR_2_METHOD)
      .actionReceiverId(ACTOR_3_ID)
      .build();
  public static final Transition TRANSITION_0_2 = Transition.builder()
      .from(GLOBAL_STATE_0)
      .arc(ARC_0_2)
      .to(GLOBAL_STATE_2)
      .build();

  public static final Arc ARC_2_3 = Arc.builder()
      .actionPerformerId(ACTOR_3_ID)
      .methodName(ACTOR_3_METHOD)
      .build();
  public static final Transition TRANSITION_2_3 = Transition.builder()
      .from(GLOBAL_STATE_2)
      .arc(ARC_2_3)
      .to(GLOBAL_STATE_3)
      .build();

  public static final Arc ARC_3_4 = Arc.builder()
      .actionPerformerId(ACTOR_1_ID)
      .methodName(ACTOR_1_METHOD)
      .build();
  public static final Transition TRANSITION_3_4 = Transition.builder()
      .from(GLOBAL_STATE_3)
      .arc(ARC_3_4)
      .to(GLOBAL_STATE_4)
      .build();

  public static final Arc ARC_0_3 = Arc.builder()
      .actionPerformerId(ACTOR_2_ID)
      .methodName(ACTOR_2_METHOD_2)
      .build();
  public static final Transition TRANSITION_0_3 = Transition.builder()
      .from(GLOBAL_STATE_0)
      .arc(ARC_0_3)
      .to(GLOBAL_STATE_3)
      .build();
}
