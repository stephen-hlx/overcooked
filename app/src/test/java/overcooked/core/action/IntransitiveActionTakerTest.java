package overcooked.core.action;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import overcooked.core.actor.ActorDefinition;
import overcooked.sample.diehard.model.Jar3;
import overcooked.sample.diehard.model.Jar5;
import overcooked.sample.diehard.modelverifier.Jar3State;

import static org.mockito.Mockito.*;

class IntransitiveActionTakerTest {
    private final ActionTemplateMaterialiser actionTemplateMaterialiser = mock(ActionTemplateMaterialiser.class);
    private final ActionTaker actionTaker = mock(ActionTaker.class);
    private final InOrder inOrder = inOrder(actionTemplateMaterialiser, actionTaker);
    private final IntransitiveActionTaker intransitiveActionTaker =
        new IntransitiveActionTaker(actionTemplateMaterialiser, actionTaker);

    @Test
    void materialises_the_template_before_calling_action_taker() {
        ActionTemplate actionTemplate = ActionTemplate.builder()
            .actionType(new TransitiveActionType(ActorDefinition.builder()
                .id("doesn't matter in this case")
                .type(Jar3.class)
                .localStateType(Jar3State.class)
                .build()))
            .build();

        Jar5 jar5 = new Jar5(0);

        ActionDefinition someAction = ActionDefinition.builder()
            .methodName("someAction")
            .build();

        when(actionTemplateMaterialiser.materialise(actionTemplate))
            .thenReturn(someAction);

        intransitiveActionTaker.take(IntransitiveAction.builder()
            .actor(jar5)
            .actionTemplate(actionTemplate)
            .build());

        inOrder.verify(actionTemplateMaterialiser, times(1)).materialise(actionTemplate);
        inOrder.verify(actionTaker, times(1)).take(jar5, someAction);
        verifyNoMoreInteractions(actionTemplateMaterialiser, actionTaker);
    }
}