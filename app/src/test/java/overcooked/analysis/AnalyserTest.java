package overcooked.analysis;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Value;
import org.junit.jupiter.api.Test;
import overcooked.analysis.Analyser;
import overcooked.analysis.Arc;
import overcooked.analysis.GlobalStateNode;
import overcooked.analysis.Transition;
import overcooked.core.GlobalState;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyserTest {
    private final Analyser analyser = new Analyser();

    @Test
    void builds_graph_correctly() {
        String actor1Id = "actor1";
        String actor2Id = "actor2";
        String actor3Id = "actor3";
        String actor4Id = "actor4";
        String actor1Method = "actor1.method1";
        String actor2Method = "actor2.method1";
        String actor4Method = "actor4.method1";
        ActorDefinition actor1 = ActorDefinition.builder().id(actor1Id).build();
        ActorDefinition actor2 = ActorDefinition.builder().id(actor2Id).build();
        ActorDefinition actor3 = ActorDefinition.builder().id(actor3Id).build();
        ActorDefinition actor4 = ActorDefinition.builder().id(actor4Id).build();

        LocalState actor1LocalState = new TestLocalState(1, 0);
        LocalState actor2LocalState = new TestLocalState(2, 0);
        LocalState actor3LocalState = new TestLocalState(3, 0);
        LocalState actor4LocalState = new TestLocalState(4, 0);
        LocalState newActor1LocalState = new TestLocalState(1, 1);
        LocalState newActor2LocalState = new TestLocalState(2, 1);
        LocalState newActor3LocalState = new TestLocalState(3, 1);

        GlobalState globalState = new GlobalState(
            ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, actor1LocalState)
                .put(actor2, actor2LocalState)
                .put(actor3, actor3LocalState)
                .put(actor4, actor4LocalState)
                .build());

        GlobalStateNode globalStateNode1 = new GlobalStateNode(globalState);
        GlobalStateNode globalStateNode2 = new GlobalStateNode(
            new GlobalState(ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, newActor1LocalState)
                .put(actor2, actor2LocalState)
                .put(actor3, actor3LocalState)
                .put(actor4, actor4LocalState)
                .build())
        );
        GlobalStateNode globalStateNode3 = new GlobalStateNode(
            new GlobalState(ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, actor1LocalState)
                .put(actor2, newActor2LocalState)
                .put(actor3, newActor3LocalState)
                .put(actor4, actor4LocalState)
                .build())
        );
        globalStateNode1.addArc(Arc.builder()
            .actionPerformerId(actor1Id)
            .methodName(actor1Method)
            .build(), globalStateNode2);
        globalStateNode1.addArc(Arc.builder()
            .actionPerformerId(actor2Id)
            .methodName(actor2Method)
            .actionReceiverId(actor3Id)
            .build(), globalStateNode3);
        globalStateNode1.addArc(Arc.builder()
            .actionPerformerId(actor4Id)
            .methodName(actor4Method)
            .build(), globalStateNode1);

        analyser.capture(Transition.builder()
            .from(globalState)
            .actionPerformerId(actor1Id)
            .methodName(actor1Method)
            .actionReceiverId(null)
            .to(new GlobalState(ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, newActor1LocalState)
                .put(actor2, actor2LocalState)
                .put(actor3, actor3LocalState)
                .put(actor4, actor4LocalState)
                .build()))
            .build());
        analyser.capture(Transition.builder()
            .from(globalState)
            .actionPerformerId(actor2Id)
            .methodName(actor2Method)
            .actionReceiverId(actor3Id)
            .to(new GlobalState(ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, actor1LocalState)
                .put(actor2, newActor2LocalState)
                .put(actor3, newActor3LocalState)
                .put(actor4, actor4LocalState)
                .build()))
            .build());
        analyser.capture(Transition.builder()
            .from(globalState)
            .actionPerformerId(actor4Id)
            .methodName(actor4Method)
            .actionReceiverId(null)
            .to(new GlobalState(ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, actor1LocalState)
                .put(actor2, actor2LocalState)
                .put(actor3, actor3LocalState)
                .put(actor4, actor4LocalState)
                .build()))
            .build());

        // the node may not take into account fields other than id
        // Comparing field by field is needed because
        assertThat(analyser.getNodes())
            .usingFieldByFieldElementComparator()
            .isEqualTo(ImmutableSet.builder()
            .add(globalStateNode1)
            .add(globalStateNode2)
            .add(globalStateNode3)
            .build());
    }

    @Value
    private static class TestLocalState implements LocalState {
        int a;
        int b;
    }
}