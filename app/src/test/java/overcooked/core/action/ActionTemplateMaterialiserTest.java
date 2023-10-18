package overcooked.core.action;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorDefinition;
import overcooked.sample.diehard.model.Jar3;
import overcooked.sample.diehard.modelverifier.Jar3State;

import static org.assertj.core.api.Assertions.assertThat;

class ActionTemplateMaterialiserTest {
    @Test
    void call_with_filling_value_works() {
        Jar3 jar3 = new Jar3(0);
        ActorDefinition jarActor = ActorDefinition.builder()
            .id("someId")
            .type(Jar3.class)
            .localStateType(Jar3State.class)
            .build();
        ActionTemplate template = ActionTemplate.builder()
            .actionType(new TransitiveActionType(jarActor))
            .methodName("someMethod")
            .parameters(ImmutableList.of(
                new ParamTemplate<>(Jar3.class),
                new ParamValue(Integer.class, 1)
            ))
            .build();

        assertThat(new ActionTemplateMaterialiser().materialise(template, Jar3.class, jar3))
            .isEqualTo(ActionDefinition.builder()
                .actionType(new TransitiveActionType(jarActor))
                .methodName("someMethod")
                .parameters(ImmutableList.of(
                    new ParamValue(Jar3.class, jar3),
                    new ParamValue(Integer.class, 1)
                ))
                .build());
    }

    @Test
    void call_without_filling_value_works() {
        Jar3 jar3 = new Jar3(0);
        ActorDefinition jarActor = ActorDefinition.builder()
            .id("someId")
            .type(Jar3.class)
            .localStateType(Jar3State.class)
            .build();
        ActionTemplate template = ActionTemplate.builder()
            .actionType(new TransitiveActionType(jarActor))
            .methodName("someMethod")
            .parameters(ImmutableList.of(
                new ParamValue(Integer.class, 1)
            ))
            .build();

        assertThat(new ActionTemplateMaterialiser().materialise(template, Jar3.class, jar3))
            .isEqualTo(ActionDefinition.builder()
                .actionType(new TransitiveActionType(jarActor))
                .methodName("someMethod")
                .parameters(ImmutableList.of(
                    new ParamValue(Integer.class, 1)
                ))
                .build());
    }
}