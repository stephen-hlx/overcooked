package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import lombok.Value;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Test;

class ActionTakerTest {
  private final ActionTaker actionTaker = new ActionTaker();

  @Test
  void can_perform_action_without_params() {
    MutableInt data = new MutableInt(0);
    ActionPerformer actionPerformer= new ActionPerformer(data);
    assertThat(actionPerformer.data.getValue()).isEqualTo(0);

    actionTaker.take(actionPerformer, ActionDefinition.builder()
        .methodName("intransitiveAction")
        .parameters(ImmutableList.of())
        .build());

    assertThat(actionPerformer.data.getValue()).isEqualTo(1);
  }

  @Test
  void can_perform_action_with_params() {
    ActionPerformer actionPerformer= new ActionPerformer(null);
    ActionReceiver actionReceiver = new ActionReceiver(new MutableInt(0));

    assertThat(actionReceiver.data.getValue()).isEqualTo(0);

    actionTaker.take(actionPerformer, ActionDefinition.builder()
        .methodName("transitiveAction")
        .parameters(ImmutableList.of(new ParamValue(ActionReceiver.class, actionReceiver)))
        .build());

    assertThat(actionReceiver.data.getValue()).isEqualTo(1);
  }

  @Value
  private static class ActionPerformer {
    MutableInt data;
    public void intransitiveAction() {
      data.increment();
    }
    public void transitiveAction(ActionReceiver actionReceiver) {
      actionReceiver.data.increment();
    }
  }

  @Value
  private static class ActionReceiver {
    MutableInt data;
  }
}