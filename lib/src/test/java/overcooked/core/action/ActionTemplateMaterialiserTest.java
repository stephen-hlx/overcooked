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
    Class<Integer> placeHolderType = Integer.class;
    Integer placeHolderValue = 1;

    when(actionType.isTransitive()).thenReturn(true);

    ActionTemplate template = ActionTemplate.builder()
        .actionType(actionType)
        .methodName("someMethod")
        .parameter(new ParamTemplate<>(placeHolderType))
        .build();

    assertThat(materialiser.materialise(template, placeHolderValue))
        .isEqualTo(ActionDefinition.builder()
            .actionType(actionType)
            .methodName("someMethod")
            .paramValue(new ParamValue(placeHolderType, placeHolderValue))
            .build());
  }

  @Test
  void call_without_filling_value_works() {
    when(actionType.isTransitive()).thenReturn(false);
    assertThat(materialiser.materialise(ActionTemplate.builder()
        .actionType(actionType)
        .methodName("someMethod")
        .parameter(null)
        .build()))
        .isEqualTo(ActionDefinition.builder()
            .actionType(actionType)
            .methodName("someMethod")
            .paramValue(null)
            .build());
  }
}