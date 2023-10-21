package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import overcooked.sample.diehard.model.Jar3;
import overcooked.sample.diehard.model.Jar5;

class ActionTakerTest {

  @Test
  void can_perform_action_without_params() {
    ActionDefinition fillDefinition = ActionDefinition.builder()
        .methodName("fill")
        .parameters(ImmutableList.of())
        .build();

    Jar5 jar5 = new Jar5(0);

    assertThat(jar5.getOccupancy()).isEqualTo(0);

    ActionTaker actionTaker = new ActionTaker();

    actionTaker.take(jar5, fillDefinition);

    assertThat(jar5.getOccupancy()).isEqualTo(5);
  }

  @Test
  void can_perform_action_with_params() {
    Jar5 jar5 = new Jar5(0);
    jar5.fill();
    Jar3 jar3 = new Jar3(0);

    ActionDefinition addToJar3 = ActionDefinition.builder()
        .methodName("addTo")
        .parameters(ImmutableList.of(new ParamValue(Jar3.class, jar3)))
        .build();

    assertThat(jar5.getOccupancy()).isEqualTo(5);
    assertThat(jar3.getOccupancy()).isEqualTo(0);

    ActionTaker actionTaker = new ActionTaker();
    actionTaker.take(jar5, addToJar3);

    assertThat(jar5.getOccupancy()).isEqualTo(2);
    assertThat(jar3.getOccupancy()).isEqualTo(3);
  }

}