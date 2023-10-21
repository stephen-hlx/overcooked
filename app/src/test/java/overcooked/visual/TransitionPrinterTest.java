package overcooked.visual;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import overcooked.analysis.Transition;
import overcooked.core.GlobalState;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

class TransitionPrinterTest {
  private final TransitionPrinter transitionPrinter =
      new TransitionPrinter(new GlobalStatePrinter());

  @Test
  void transition_of_transitive_action_prints_correctly() {
    String actor1Id = "actor1";
    String actor2Id = "actor2";

    ActorDefinition actor1 = ActorDefinition.builder().id(actor1Id).type(Integer.class).build();
    ActorDefinition actor2 = ActorDefinition.builder().id(actor2Id).type(Boolean.class).build();

    LocalState actor1LocalState = new TestLocalState(1, 0);
    LocalState actor2LocalState = new TestLocalState(2, 0);
    LocalState newActor1LocalState = new TestLocalState(1, 1);
    LocalState newActor2LocalState = new TestLocalState(2, 1);

    assertThat(transitionPrinter.print(Transition.builder()
        .from(new GlobalState(
            ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, actor1LocalState)
                .put(actor2, actor2LocalState)
                .build()))
        .actionPerformerId(actor1Id)
        .methodName("transitiveMethod")
        .actionReceiverId(actor2Id)
        .to(new GlobalState(
            ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, newActor1LocalState)
                .put(actor2, newActor2LocalState)
                .build()))
        .build()))
        .isEqualTo("\"actor2(f1=2,f2=0), actor1(f1=1,f2=0)\" "
            + "-> \"actor2(f1=2,f2=1), actor1(f1=1,f2=1)\" "
            + "[label=\"actor1.transitiveMethod(actor2)\"]");
  }

  @Test
  void transition_of_intransitive_action_prints_correctly() {
    String actor1Id = "actor1";
    String actor2Id = "actor2";

    ActorDefinition actor1 = ActorDefinition.builder().id(actor1Id).type(Integer.class).build();
    ActorDefinition actor2 = ActorDefinition.builder().id(actor2Id).type(Boolean.class).build();

    LocalState actor1LocalState = new TestLocalState(1, 0);
    LocalState actor2LocalState = new TestLocalState(2, 0);
    LocalState newActor1LocalState = new TestLocalState(1, 1);
    LocalState newActor2LocalState = new TestLocalState(2, 0);

    assertThat(transitionPrinter.print(Transition.builder()
        .from(new GlobalState(
            ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, actor1LocalState)
                .put(actor2, actor2LocalState)
                .build()))
        .actionPerformerId(actor1Id)
        .methodName("intransitiveMethod")
        .to(new GlobalState(
            ImmutableMap.<ActorDefinition, LocalState>builder()
                .put(actor1, newActor1LocalState)
                .put(actor2, newActor2LocalState)
                .build()))
        .build()))
        .isEqualTo("\"actor2(f1=2,f2=0), actor1(f1=1,f2=0)\" -> "
            + "\"actor2(f1=2,f2=0), actor1(f1=1,f2=1)\" "
            + "[label=\"actor1.intransitiveMethod()\"]");
  }
}