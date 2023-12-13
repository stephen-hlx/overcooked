package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.Actor;

class ActionTakerTest {
  private final ActionTaker actionTaker = new ActionTaker();

  @Test
  void rethrows_exception_when_transitive_action_throws_exception() {
    assertThat(actionTaker.take(new ActionPerformer(null),
        ActionDefinition.<ActionPerformer, ActionReceiver>builder()
            .actionType(new TransitiveActionType(Actor.builder().id("actionReceiver").build()))
            .action(ActionPerformer::exceptionThrowingMethod)
            .actionReceiver(new ActionReceiver(null))
            .actionPerformerDefinition(Actor.builder().id("not used").build())
            .actionLabel("not used")
            .build()))
        .isEqualTo(ActionResult.failure(new MyException()));
  }

  @Test
  void rethrows_exception_when_intransitive_action_throws_exception() {
    assertThat(actionTaker.take(new ActionPerformer(null),
        ActionDefinition.<ActionPerformer, Void>builder()
            .actionType(new IntransitiveActionType())
            .action((actionPerformer, unused) -> actionPerformer.exceptionThrowingMethod())
            .actionPerformerDefinition(Actor.builder().id("not used").build())
            .actionLabel("not used")
            .build()))
        .isEqualTo(ActionResult.failure(new MyException()));
  }

  @Test
  void can_perform_action_without_action_receiver() {
    MutableInt data = new MutableInt(0);
    ActionPerformer actionPerformer = new ActionPerformer(data);
    assertThat(actionPerformer.data.getValue()).isEqualTo(0);

    assertThat(actionTaker.take(actionPerformer, ActionDefinition.<ActionPerformer, Void>builder()
        .actionType(new IntransitiveActionType())
        .action((actionPerformer1, unused) -> actionPerformer.intransitiveAction())
        .actionPerformerDefinition(Actor.builder().id("not used").build())
        .actionLabel("not used")
        .build()))
        .isEqualTo(ActionResult.success());

    assertThat(actionPerformer.data.getValue()).isEqualTo(1);
  }

  @Test
  void can_perform_action_with_action_receiver() {
    ActionPerformer actionPerformer = new ActionPerformer(new MutableInt(0));
    ActionReceiver actionReceiver = new ActionReceiver(new MutableInt(0));

    assertThat(actionReceiver.data.getValue()).isEqualTo(0);

    assertThat(actionTaker.take(actionPerformer,
        ActionDefinition.<ActionPerformer, ActionReceiver>builder()
            .actionPerformerDefinition(Actor.builder().id("not used").build())
            .actionLabel("not used")
            .actionType(new TransitiveActionType(Actor.builder().id("doesn't matter").build()))
            .action(ActionPerformer::transitiveAction)
            .actionReceiver(actionReceiver).build()))
        .isEqualTo(ActionResult.success());

    assertThat(actionPerformer.data.getValue()).isEqualTo(1);
    assertThat(actionReceiver.data.getValue()).isEqualTo(1);
  }

  @Value
  private static class ActionPerformer {
    MutableInt data;

    public void exceptionThrowingMethod() throws MyException {
      throw new MyException();
    }

    public void exceptionThrowingMethod(ActionReceiver notUsed) throws MyException {
      exceptionThrowingMethod();
    }

    public void intransitiveAction() {
      data.increment();
    }

    public void transitiveAction(ActionReceiver actionReceiver) {
      data.increment();
      actionReceiver.data.increment();
    }
  }

  @Value
  @SuppressWarnings("unused")
  private static class ActionReceiver {
    MutableInt data;
  }

  @EqualsAndHashCode(callSuper = false)
  private static class MyException extends RuntimeException {
  }
}