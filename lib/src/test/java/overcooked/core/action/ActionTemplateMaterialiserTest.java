package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class ActionTemplateMaterialiserTest {
  private final ActionType actionType = mock(ActionType.class);
  private final ActionTemplateMaterialiser materialiser = new ActionTemplateMaterialiser();

  @Test
  void call_with_filling_value_works() {
    Integer actionReceiver = 1;

    when(actionType.isTransitive()).thenReturn(true);

    ActionTemplate<Void, Integer> template = ActionTemplate.<Void, Integer>builder()
        .actionType(actionType)
        .methodName("someMethod")
        .build();

    assertThat(materialiser.materialise(template, actionReceiver))
        .isEqualTo(ActionDefinition.<Void, Integer>builder()
            .actionType(actionType)
            .methodName("someMethod")
            .actionReceiver(actionReceiver)
            .build());
  }

  @Test
  void call_without_filling_value_works() {
    when(actionType.isTransitive()).thenReturn(false);
    assertThat(materialiser.materialise(ActionTemplate.builder()
        .actionType(actionType)
        .methodName("someMethod")
        .build()))
        .isEqualTo(ActionDefinition.builder()
            .actionType(actionType)
            .methodName("someMethod")
            .actionReceiver(null)
            .build());
  }
}