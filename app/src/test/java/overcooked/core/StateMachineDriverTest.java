package overcooked.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import lombok.Value;
import org.junit.jupiter.api.Test;
import overcooked.analysis.Arc;
import overcooked.analysis.StateMachineExecutionDataCollector;
import overcooked.analysis.Transition;
import overcooked.core.action.ActionTemplate;
import overcooked.core.action.IntransitiveActionTemplateExecutor;
import overcooked.core.action.IntransitiveActionType;
import overcooked.core.action.ParamTemplate;
import overcooked.core.action.TransitiveActionTemplateExecutor;
import overcooked.core.action.TransitiveActionType;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

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
    ActorDefinition actor1 = ActorDefinition.builder().id(actor1Id).build();
    ActorDefinition actor2 = ActorDefinition.builder().id(actor2Id).build();
    ActorDefinition actor3 = ActorDefinition.builder().id(actor3Id).build();
    ActorDefinition actor4 = ActorDefinition.builder().id(actor4Id).build();

    ActionTemplate actor1ActionTemplate = ActionTemplate.builder()
        .actionType(new IntransitiveActionType())
        .methodName(actor1Method)
        .build();
    ActionTemplate actor2ActionTemplate = ActionTemplate.builder()
        .actionType(new TransitiveActionType(actor3))
        .methodName(actor2Method)
        .parameters(ImmutableList.of(new ParamTemplate<>(Integer.class)))
        .build();
    ActorActionConfig config = new ActorActionConfig(
        ImmutableMap.<ActorDefinition, Set<ActionTemplate>>builder()
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
        .thenReturn(ImmutableMap.of(actor1, newActor1LocalState));
    when(transitiveActionTemplateExecutor.execute(actor2LocalState, actor2, actor3LocalState,
        actor2ActionTemplate))
        .thenReturn(ImmutableMap.of(
            actor2, newActor2LocalState,
            actor3, newActor3LocalState
        ));

    GlobalState globalState = new GlobalState(
        ImmutableMap.<ActorDefinition, LocalState>builder()
            .put(actor1, actor1LocalState)
            .put(actor2, actor2LocalState)
            .put(actor3, actor3LocalState)
            .put(actor4, actor4LocalState)
            .build());

    StateMachineExecutionDataCollector
        stateMachineExecutionDataCollector = mock(StateMachineExecutionDataCollector.class);

    assertThat(stateMachineDriver.computeNext(globalState, config,
        stateMachineExecutionDataCollector))
        .isEqualTo(ImmutableSet.of(
            new GlobalState(ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, newActor1LocalState)
                .put(actor2, actor2LocalState)
                .put(actor3, actor3LocalState)
                .put(actor4, actor4LocalState)
                .build()),
            new GlobalState(ImmutableMap.<ActorDefinition, LocalState>builder()
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
    verify(stateMachineExecutionDataCollector).capture(Transition.builder()
        .from(globalState)
        .arc(Arc.builder()
            .actionPerformerId(actor1Id)
            .methodName(actor1Method)
            .actionReceiverId(null)
            .build())
        .to(new GlobalState(ImmutableMap.<ActorDefinition, LocalState>builder()
            .put(actor1, newActor1LocalState)
            .put(actor2, actor2LocalState)
            .put(actor3, actor3LocalState)
            .put(actor4, actor4LocalState)
            .build()))
        .build());
    verify(stateMachineExecutionDataCollector).capture(Transition.builder()
        .from(globalState)
        .arc(Arc.builder()
            .actionPerformerId(actor2Id)
            .methodName(actor2Method)
            .actionReceiverId(actor3Id)
            .build())
        .to(new GlobalState(ImmutableMap.<ActorDefinition, LocalState>builder()
            .put(actor1, actor1LocalState)
            .put(actor2, newActor2LocalState)
            .put(actor3, newActor3LocalState)
            .put(actor4, actor4LocalState)
            .build()))
        .build());
    verifyNoMoreInteractions(intransitiveActionTemplateExecutor,
        transitiveActionTemplateExecutor,
        stateMerger,
        stateMachineExecutionDataCollector);
  }

  @Value
  private static class TestLocalState implements LocalState {
    int f1;
    int f2;
  }
}