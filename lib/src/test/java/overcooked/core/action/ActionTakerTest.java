package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Test;

class ActionTakerTest {
  private final ActionTaker actionTaker = new ActionTaker();

  @Test
  void suppresses_IllegalAccessException() {
    assertThat(actionTaker.take(new ActionPerformer(null), ActionDefinition.builder()
        .methodName("nonPublicMethod")
        .build())).isEqualTo(ActionResult.success());
  }

  @Test
  void throws_InvocationTargetException_when_method_throws_checked_exception() {
    assertThat(actionTaker.take(new ActionPerformer(null), ActionDefinition.builder()
        .methodName("checkedExceptionThrowingMethod")
        .build()))
        .isEqualTo(ActionResult.failure(new MyCheckedException()));
  }

  @Test
  void throws_InvocationTargetException_when_method_throws_unchecked_exception() {
    assertThat(actionTaker.take(new ActionPerformer(null), ActionDefinition.builder()
        .methodName("uncheckedExceptionThrowingMethod")
        .build()))
        .isEqualTo(ActionResult.failure(new MyUncheckedException()));
  }

  @Test
  void can_perform_action_without_params() {
    MutableInt data = new MutableInt(0);
    ActionPerformer actionPerformer = new ActionPerformer(data);
    assertThat(actionPerformer.data.getValue()).isEqualTo(0);

    actionTaker.take(actionPerformer, ActionDefinition.builder()
        .methodName("intransitiveAction")
        .build());

    assertThat(actionPerformer.data.getValue()).isEqualTo(1);
  }

  @Test
  void can_perform_action_with_params() {
    ActionPerformer actionPerformer = new ActionPerformer(null);
    ActionReceiver actionReceiver = new ActionReceiver(new MutableInt(0));

    assertThat(actionReceiver.data.getValue()).isEqualTo(0);

    actionTaker.take(actionPerformer, ActionDefinition.builder()
        .methodName("transitiveAction")
        .paramValue(new ParamValue(ActionReceiver.class, actionReceiver))
        .build());

    assertThat(actionReceiver.data.getValue()).isEqualTo(1);
  }

  @Value
  @SuppressWarnings("unused")
  private static class ActionPerformer {
    MutableInt data;

    public void nonPublicMethod() {
    }

    public void checkedExceptionThrowingMethod() throws MyCheckedException {
      throw new MyCheckedException();
    }

    public void uncheckedExceptionThrowingMethod() {
      throw new MyUncheckedException();
    }

    public void intransitiveAction() {
      data.increment();
    }

    public void transitiveAction(ActionReceiver actionReceiver) {
      actionReceiver.data.increment();
    }
  }

  @Value
  @SuppressWarnings("unused")
  private static class ActionReceiver {
    MutableInt data;
  }

  @EqualsAndHashCode(callSuper = false)
  private static class MyCheckedException extends Exception {
  }

  @EqualsAndHashCode(callSuper = false)
  private static class MyUncheckedException extends RuntimeException {
  }
}