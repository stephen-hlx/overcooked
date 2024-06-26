package overcooked.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import overcooked.core.action.ActionResult;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.ExecutionResult;
import overcooked.core.action.IntransitiveActionTemplateExecutor;
import overcooked.core.action.IntransitiveActionType;
import overcooked.core.action.TransitiveActionTemplateExecutor;
import overcooked.core.action.TransitiveActionType;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;
import overcooked.core.actor.LocalState;
import overcooked.util.TestActorState;

/**
 * ls == local_state
 * (ls_1_0, ls_2_0, ls_3_0, ls_4_0) -- actor1.method1 --> (ls_1_1, ls_2_0, ls_3_0, ls_4_0)
 * (ls_1_0, ls_2_0, ls_3_0, ls_4_0) -- actor2.method1(actor3) --> (ls_1_0, ls_2_1, ls_3_1, ls_4_0)
 */
class StateMachineDriverTest {
  private static final String ACTOR_1_METHOD_1 = "actor1.method1";
  private static final String ACTOR_2_METHOD_1 = "actor2.method1";
  private static final ActorId ACTOR_ID_1 = new ActorId("actor1");
  private static final ActorId ACTOR_ID_2 = new ActorId("actor2");
  private static final ActorId ACTOR_ID_3 = new ActorId("actor3");
  private static final ActorId ACTOR_ID_4 = new ActorId("actor4");

  private static final ActionTemplate<?, ?> ACTOR_1_ACTION_TEMPLATE = ActionTemplate.builder()
      .actionPerformerId(ACTOR_ID_1)
      .actionType(new IntransitiveActionType())
      .actionLabel(ACTOR_1_METHOD_1)
      .action((notUsed1, notUsed2) -> { })
      .build();
  private static final ActionTemplate<?, ?> ACTOR_2_ACTION_TEMPLATE =
      ActionTemplate.<Void, Integer>builder()
          .actionPerformerId(ACTOR_ID_2)
          .actionType(new TransitiveActionType(ACTOR_ID_3))
          .actionLabel(ACTOR_2_METHOD_1)
          .action((notUsed1, notUsed2) -> { })
          .build();
  private static final ActorActionConfig ACTOR_ACTION_CONFIG = new ActorActionConfig(
      ImmutableMap.<ActorId, Set<ActionTemplate<?, ?>>>builder()
          .put(ACTOR_ID_1, ImmutableSet.of(ACTOR_1_ACTION_TEMPLATE))
          .put(ACTOR_ID_2, ImmutableSet.of(ACTOR_2_ACTION_TEMPLATE))
          .build());

  private static final LocalState ACTOR_1_LOCAL_STATE = localStateOf(new TestActorState(1, 0));
  private static final LocalState ACTOR_2_LOCAL_STATE = localStateOf(new TestActorState(2, 0));
  private static final LocalState ACTOR_3_LOCAL_STATE = localStateOf(new TestActorState(3, 0));
  private static final LocalState ACTOR_4_LOCAL_STATE = localStateOf(new TestActorState(4, 0));
  private static final LocalState NEW_ACTOR_1_LOCAL_STATE = localStateOf(new TestActorState(1, 1));
  private static final LocalState NEW_ACTOR_2_LOCAL_STATE = localStateOf(new TestActorState(2, 1));
  private static final LocalState NEW_ACTOR_3_LOCAL_STATE = localStateOf(new TestActorState(3, 1));

  private final IntransitiveActionTemplateExecutor intransitiveActionTemplateExecutor =
      mock(IntransitiveActionTemplateExecutor.class);
  private final TransitiveActionTemplateExecutor transitiveActionTemplateExecutor =
      mock(TransitiveActionTemplateExecutor.class);

  private final StateMerger stateMerger = spy(new StateMerger());
  private final StateMachineDriver stateMachineDriver = StateMachineDriver.builder()
      .intransitiveActionTemplateExecutor(intransitiveActionTemplateExecutor)
      .transitiveActionTemplateExecutor(transitiveActionTemplateExecutor)
      .stateMerger(stateMerger)
      .build();

  @Test
  void works() {
    when(intransitiveActionTemplateExecutor.execute(ACTOR_1_ACTION_TEMPLATE, ACTOR_1_LOCAL_STATE))
        .thenReturn(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(ACTOR_ID_1, NEW_ACTOR_1_LOCAL_STATE))
            .build());
    when(transitiveActionTemplateExecutor
        .execute(ACTOR_2_ACTION_TEMPLATE, ACTOR_2_LOCAL_STATE, ACTOR_3_LOCAL_STATE))
        .thenReturn(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(
                ACTOR_ID_2, NEW_ACTOR_2_LOCAL_STATE,
                ACTOR_ID_3, NEW_ACTOR_3_LOCAL_STATE
            )).build());

    GlobalState initialState = new GlobalState(
        ImmutableMap.<ActorId, LocalState>builder()
            .put(ACTOR_ID_1, ACTOR_1_LOCAL_STATE)
            .put(ACTOR_ID_2, ACTOR_2_LOCAL_STATE)
            .put(ACTOR_ID_3, ACTOR_3_LOCAL_STATE)
            .put(ACTOR_ID_4, ACTOR_4_LOCAL_STATE)
            .build());

    StateMachineExecutionContext stateMachineExecutionContext =
        spy(new StateMachineExecutionContext(initialState));

    GlobalState globalState1 = new GlobalState(ImmutableMap.<ActorId, LocalState>builder()
        .put(ACTOR_ID_1, NEW_ACTOR_1_LOCAL_STATE)
        .put(ACTOR_ID_2, ACTOR_2_LOCAL_STATE)
        .put(ACTOR_ID_3, ACTOR_3_LOCAL_STATE)
        .put(ACTOR_ID_4, ACTOR_4_LOCAL_STATE)
        .build());
    GlobalState globalState2 = new GlobalState(ImmutableMap.<ActorId, LocalState>builder()
        .put(ACTOR_ID_1, ACTOR_1_LOCAL_STATE)
        .put(ACTOR_ID_2, NEW_ACTOR_2_LOCAL_STATE)
        .put(ACTOR_ID_3, NEW_ACTOR_3_LOCAL_STATE)
        .put(ACTOR_ID_4, ACTOR_4_LOCAL_STATE)
        .build());

    assertThat(stateMachineDriver.computeNext(initialState, ACTOR_ACTION_CONFIG,
        stateMachineExecutionContext))
        .isEqualTo(ImmutableSet.of(globalState1, globalState2));

    verify(intransitiveActionTemplateExecutor)
        .execute(ACTOR_1_ACTION_TEMPLATE, ACTOR_1_LOCAL_STATE);
    verify(transitiveActionTemplateExecutor)
        .execute(ACTOR_2_ACTION_TEMPLATE, ACTOR_2_LOCAL_STATE, ACTOR_3_LOCAL_STATE);
    verify(stateMerger).merge(initialState, ImmutableMap.of(ACTOR_ID_1, NEW_ACTOR_1_LOCAL_STATE));
    verify(stateMerger).merge(initialState, ImmutableMap.of(
        ACTOR_ID_2, NEW_ACTOR_2_LOCAL_STATE,
        ACTOR_ID_3, NEW_ACTOR_3_LOCAL_STATE));
    verify(stateMachineExecutionContext).registerOrGetDuplicate(globalState1);
    verify(stateMachineExecutionContext).capture(initialState,
        ACTOR_1_ACTION_TEMPLATE,
        globalState1,
        ActionResult.success());
    verify(stateMachineExecutionContext).registerOrGetDuplicate(globalState2);
    verify(stateMachineExecutionContext).capture(initialState,
        ACTOR_2_ACTION_TEMPLATE,
        globalState2,
        ActionResult.success());
    verifyNoMoreInteractions(intransitiveActionTemplateExecutor,
        transitiveActionTemplateExecutor,
        stateMerger,
        stateMachineExecutionContext);
  }

  private static LocalState localStateOf(ActorState actorState) {
    return LocalState.builder()
        .actorState(actorState)
        .build();
  }
}