package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import overcooked.core.actor.Actor;


class IntransitiveActionTakerTest {
  private final ActionTemplateMaterialiser actionTemplateMaterialiser =
      mock(ActionTemplateMaterialiser.class);
  private final ActionTaker actionTaker = mock(ActionTaker.class);
  private final InOrder inOrder = inOrder(actionTemplateMaterialiser, actionTaker);
  private final IntransitiveActionTaker intransitiveActionTaker =
      new IntransitiveActionTaker(actionTemplateMaterialiser, actionTaker);

  @Test
  void materialises_the_template_before_calling_action_taker() {
    ActionTemplate<String, Integer> actionTemplate = ActionTemplate.<String, Integer>builder()
        .actionPerformerDefinition(Actor.builder().id("not used").build())
        .action((notUsed1, notUsed2) -> {})
        .actionType(new IntransitiveActionType())
        .actionLabel("not used")
        .build();

    ActionDefinition<String, Integer> someAction = ActionDefinition.<String, Integer>builder()
        .actionPerformerDefinition(Actor.builder().id("not used").build())
        .action((notUsed1, notUsed2) -> {})
        .actionType(new IntransitiveActionType())
        .actionLabel("someAction")
        .actionReceiver(1)
        .build();

    String actionPerformer = "";

    when(actionTemplateMaterialiser.materialise(actionTemplate))
        .thenReturn(someAction);
    when(actionTaker.take(actionPerformer, someAction)).thenReturn(ActionResult.success());

    assertThat(intransitiveActionTaker.take(IntransitiveAction.<String, Integer>builder()
        .actor(actionPerformer)
        .actionTemplate(actionTemplate)
        .build()))
        .isEqualTo(ActionResult.success());

    inOrder.verify(actionTemplateMaterialiser).materialise(actionTemplate);
    inOrder.verify(actionTaker).take(actionPerformer, someAction);
    verifyNoMoreInteractions(actionTemplateMaterialiser, actionTaker);
  }
}