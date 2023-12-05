package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;


class IntransitiveActionTakerTest {
  private final ActionTemplateMaterialiser actionTemplateMaterialiser =
      mock(ActionTemplateMaterialiser.class);
  private final ActionTaker actionTaker = mock(ActionTaker.class);
  private final InOrder inOrder = inOrder(actionTemplateMaterialiser, actionTaker);
  private final IntransitiveActionTaker intransitiveActionTaker =
      new IntransitiveActionTaker(actionTemplateMaterialiser, actionTaker);

  @Test
  void materialises_the_template_before_calling_action_taker() {
    ActionTemplate actionTemplate = ActionTemplate.builder()
        .actionType(new IntransitiveActionType())
        .build();

    ActionDefinition someAction = ActionDefinition.builder()
        .methodName("someAction")
        .build();

    Object actor = new Object();

    when(actionTemplateMaterialiser.materialise(actionTemplate))
        .thenReturn(someAction);
    when(actionTaker.take(actor, someAction)).thenReturn(ActionResult.success());

    assertThat(intransitiveActionTaker.take(IntransitiveAction.builder()
        .actor(actor)
        .actionTemplate(actionTemplate)
        .build()))
        .isEqualTo(ActionResult.success());

    inOrder.verify(actionTemplateMaterialiser).materialise(actionTemplate);
    inOrder.verify(actionTaker).take(actor, someAction);
    verifyNoMoreInteractions(actionTemplateMaterialiser, actionTaker);
  }
}