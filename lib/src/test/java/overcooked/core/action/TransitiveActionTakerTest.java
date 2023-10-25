package overcooked.core.action;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import overcooked.core.actor.ActorDefinition;
import overcooked.sample.diehard.model.Jar3;
import overcooked.sample.diehard.model.Jar5;
import overcooked.sample.diehard.modelverifier.Jar3State;


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
        .actionType(new TransitiveActionType(ActorDefinition.builder()
            .id("doesn't matter in this case")
            .type(Jar3.class)
            .localStateType(Jar3State.class)
            .build()))
        .build();

    Jar5 jar5 = new Jar5(0);
    Jar3 jar3 = new Jar3(0);

    ActionDefinition someActionAgainstJar3 = ActionDefinition.builder()
        .methodName("someActionAgainstJar3")
        .parameters(ImmutableList.of(new ParamValue(Jar3.class, jar3)))
        .build();

    when(actionTemplateMaterialiser.materialise(actionTemplate, Jar3.class, jar3))
        .thenReturn(someActionAgainstJar3);

    transitiveActionTaker.take(TransitiveAction.builder()
        .actionPerformer(jar5)
        .actionReceiver(jar3)
        .actionReceiverType(Jar3.class)
        .actionTemplate(actionTemplate)
        .build());

    inOrder.verify(actionTemplateMaterialiser, times(1))
        .materialise(actionTemplate, Jar3.class, jar3);
    inOrder.verify(actionTaker, times(1)).take(jar5, someActionAgainstJar3);
    verifyNoMoreInteractions(actionTemplateMaterialiser, actionTaker);
  }
}