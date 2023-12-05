package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import overcooked.core.actor.Actor;


class TransitiveActionTakerTest {
  private final ActionTemplateMaterialiser actionTemplateMaterialiser =
      mock(ActionTemplateMaterialiser.class);
  private final ActionTaker actionTaker = mock(ActionTaker.class);
  private final InOrder inOrder = inOrder(actionTemplateMaterialiser, actionTaker);
  private final TransitiveActionTaker transitiveActionTaker =
      new TransitiveActionTaker(actionTemplateMaterialiser, actionTaker);

  @Test
  void materialises_the_template_before_calling_action_taker() {
    ActionTemplate actionTemplate = ActionTemplate.builder()
        .actionType(new TransitiveActionType(Actor.builder()
            .id("doesn't matter in this case")
            .build()))
        .parameters(ImmutableList.of(new ParamTemplate<>(Integer.class)))
        .build();

    Object actionPerformer = "";
    Object actionReceiver = 0;


    ActionDefinition action = ActionDefinition.builder()
        .methodName("action")
        .parameters(ImmutableList.of(new ParamValue(Integer.class, actionReceiver)))
        .build();

    when(actionTemplateMaterialiser.materialise(actionTemplate, actionReceiver))
        .thenReturn(action);
    when(actionTaker.take(actionPerformer, action)).thenReturn(ActionResult.success());

    assertThat(transitiveActionTaker.take(TransitiveAction.builder()
        .actionPerformer(actionPerformer)
        .actionReceiver(actionReceiver)
        .actionTemplate(actionTemplate)
        .build()))
        .isEqualTo(ActionResult.success());

    inOrder.verify(actionTemplateMaterialiser)
        .materialise(actionTemplate, actionReceiver);
    inOrder.verify(actionTaker).take(actionPerformer, action);
    verifyNoMoreInteractions(actionTemplateMaterialiser, actionTaker);
  }
}