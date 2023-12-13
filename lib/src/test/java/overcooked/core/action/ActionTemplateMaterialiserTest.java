package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import overcooked.core.actor.Actor;

class ActionTemplateMaterialiserTest {
  private final ActionType actionType = mock(ActionType.class);
  private final ActionTemplateMaterialiser materialiser = new ActionTemplateMaterialiser();

  @Test
  void call_with_action_receiver_works() {
    Integer actionReceiver = 1;

    when(actionType.isTransitive()).thenReturn(true);

    ActionTemplate<Void, Integer> template = ActionTemplate.<Void, Integer>builder()
        .actionPerformerDefinition(Actor.builder().id("actionPerformerId").build())
        .actionType(actionType)
        .actionLabel("someMethod")
        .action((notUsed1, notUsed2) -> {})
        .build();

    assertThat(materialiser.materialise(template, actionReceiver))
        .isEqualTo(ActionDefinition.<Void, Integer>builder()
            .actionPerformerDefinition(Actor.builder().id("actionPerformerId").build())
            .actionType(actionType)
            .actionLabel("someMethod")
            .actionReceiver(actionReceiver)
            .action((notUsed1, notUsed2) -> {})
            .build());
  }

  @Test
  void call_without_action_receiver_works() {
    when(actionType.isTransitive()).thenReturn(false);
    assertThat(materialiser.materialise(ActionTemplate.builder()
        .actionPerformerDefinition(Actor.builder().id("actionPerformerId").build())
        .actionType(actionType)
        .actionLabel("someMethod")
        .action((notUsed1, notUsed2) -> {})
        .build()))
        .isEqualTo(ActionDefinition.builder()
            .actionPerformerDefinition(Actor.builder().id("actionPerformerId").build())
            .actionType(actionType)
            .actionLabel("someMethod")
            .action((notUsed1, notUsed2) -> {})
            .actionReceiver(null)
            .build());
  }
}