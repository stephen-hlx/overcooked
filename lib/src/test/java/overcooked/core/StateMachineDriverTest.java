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
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.junit.jupiter.api.Test;
import overcooked.analysis.Arc;
import overcooked.core.action.ActionResult;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.ExecutionResult;
import overcooked.core.action.IntransitiveActionTemplateExecutor;
import overcooked.core.action.IntransitiveActionType;
import overcooked.core.action.TransitiveActionTemplateExecutor;
import overcooked.core.action.TransitiveActionType;
import overcooked.core.actor.Actor;
import overcooked.core.actor.LocalState;

/**
 * TODO: refactoring needed. too cumbersome!
 */
class StateMachineDriverTest {
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
    String actor1Id = "actor1";
    String actor2Id = "actor2";
    String actor3Id = "actor3";
    String actor4Id = "actor4";
    String actor1Method = "actor1.method1";
    String actor2Method = "actor2.method1";
    Actor actor1 = Actor.builder().id(actor1Id).build();
    Actor actor2 = Actor.builder().id(actor2Id).build();
    Actor actor3 = Actor.builder().id(actor3Id).build();
    Actor actor4 = Actor.builder().id(actor4Id).build();

    ActionTemplate<?, ?> actor1ActionTemplate = ActionTemplate.builder()
        .actionType(new IntransitiveActionType())
        .actionLabel(actor1Method)
        .build();
    ActionTemplate<?, ?> actor2ActionTemplate = ActionTemplate.<Void, Integer>builder()
        .actionType(new TransitiveActionType(actor3))
        .actionLabel(actor2Method)
        .build();
    ActorActionConfig config = new ActorActionConfig(
        ImmutableMap.<Actor, Set<ActionTemplate<?, ?>>>builder()
            .put(actor1, ImmutableSet.of(actor1ActionTemplate))
            .put(actor2, ImmutableSet.of(actor2ActionTemplate))
            .build());

    LocalState actor1LocalState = new TestLocalState(1, 0);
    LocalState actor2LocalState = new TestLocalState(2, 0);
    LocalState actor3LocalState = new TestLocalState(3, 0);
    LocalState actor4LocalState = new TestLocalState(4, 0);
    LocalState newActor1LocalState = new TestLocalState(1, 1);
    LocalState newActor2LocalState = new TestLocalState(2, 1);
    LocalState newActor3LocalState = new TestLocalState(3, 1);

    when(intransitiveActionTemplateExecutor.execute(actor1LocalState, actor1, actor1ActionTemplate))
        .thenReturn(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(actor1, newActor1LocalState))
            .build());
    when(transitiveActionTemplateExecutor.execute(actor2LocalState, actor2, actor3LocalState,
        actor2ActionTemplate))
        .thenReturn(ExecutionResult.builder()
            .actionResult(ActionResult.success())
            .localStates(ImmutableMap.of(
                actor2, newActor2LocalState,
                actor3, newActor3LocalState
            )).build());

    GlobalState globalState = new GlobalState(
        ImmutableMap.<Actor, LocalState>builder()
            .put(actor1, actor1LocalState)
            .put(actor2, actor2LocalState)
            .put(actor3, actor3LocalState)
            .put(actor4, actor4LocalState)
            .build());

    StateMachineExecutionContext
        stateMachineExecutionContext = spy(new StateMachineExecutionContext(globalState));

    assertThat(stateMachineDriver.computeNext(globalState, config,
        stateMachineExecutionContext))
        .isEqualTo(ImmutableSet.of(
            new GlobalState(ImmutableMap.<Actor, LocalState>builder()
                .put(actor1, newActor1LocalState)
                .put(actor2, actor2LocalState)
                .put(actor3, actor3LocalState)
                .put(actor4, actor4LocalState)
                .build()),
            new GlobalState(ImmutableMap.<Actor, LocalState>builder()
                .put(actor1, actor1LocalState)
                .put(actor2, newActor2LocalState)
                .put(actor3, newActor3LocalState)
                .put(actor4, actor4LocalState)
                .build())
        ));

    verify(intransitiveActionTemplateExecutor).execute(actor1LocalState, actor1,
        actor1ActionTemplate);
    verify(transitiveActionTemplateExecutor)
        .execute(actor2LocalState, actor2, actor3LocalState, actor2ActionTemplate);
    verify(stateMerger).merge(globalState, ImmutableMap.of(actor1, newActor1LocalState));
    verify(stateMerger).merge(globalState, ImmutableMap.of(
        actor2, newActor2LocalState,
        actor3, newActor3LocalState
    ));
    verify(stateMachineExecutionContext).registerOrGetDuplicate(
        new GlobalState(ImmutableMap.<Actor, LocalState>builder()
            .put(actor1, newActor1LocalState)
            .put(actor2, actor2LocalState)
            .put(actor3, actor3LocalState)
            .put(actor4, actor4LocalState)
            .build()));
    verify(stateMachineExecutionContext).capture(globalState,
        Arc.builder()
            .actionPerformerId(actor1Id)
            .label(actor1Method)
            .actionReceiverId(null)
            .build(),
        new GlobalState(ImmutableMap.<Actor, LocalState>builder()
            .put(actor1, newActor1LocalState)
            .put(actor2, actor2LocalState)
            .put(actor3, actor3LocalState)
            .put(actor4, actor4LocalState)
            .build()),
        ActionResult.success());
    verify(stateMachineExecutionContext).registerOrGetDuplicate(
        new GlobalState(ImmutableMap.<Actor, LocalState>builder()
            .put(actor1, actor1LocalState)
            .put(actor2, newActor2LocalState)
            .put(actor3, newActor3LocalState)
            .put(actor4, actor4LocalState)
            .build()));
    verify(stateMachineExecutionContext).capture(globalState,
        Arc.builder()
            .actionPerformerId(actor2Id)
            .label(actor2Method)
            .actionReceiverId(actor3Id)
            .build(),
        new GlobalState(ImmutableMap.<Actor, LocalState>builder()
            .put(actor1, actor1LocalState)
            .put(actor2, newActor2LocalState)
            .put(actor3, newActor3LocalState)
            .put(actor4, actor4LocalState)
            .build()),
        ActionResult.success());
    verifyNoMoreInteractions(intransitiveActionTemplateExecutor,
        transitiveActionTemplateExecutor,
        stateMerger,
        stateMachineExecutionContext);
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  private static class TestLocalState extends LocalState {
    int f1;
    int f2;

    public String toString() {
      return String.format("f1=%d,f2=%d", f1, f2);
    }
  }
}