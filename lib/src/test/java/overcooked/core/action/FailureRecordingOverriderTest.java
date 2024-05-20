package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorBase;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.SimulatedFailure;

class FailureRecordingOverriderTest {
  private static final ActorId ACTOR_ID = new ActorId("someId");

  private final FailureRecordingOverrider overrider = new FailureRecordingOverrider();

  @Test
  void adds_provided_failure_to_rejections() {
    Map<ActorId, Set<SimulatedFailure>> rejections = new HashMap<>();
    TestActor actionPerformer = new TestActor();
    actionPerformer.rejectActionFrom(ACTOR_ID, new SimulatedFailure("failureId", obj ->
        ((TestActor) obj).someAction(), new RuntimeException()));

    assertThat(rejections).as("injection has not been executed").isEmpty();

    TestActor updatedActionPerformer = overrider.override(actionPerformer, rejections);

    updatedActionPerformer.rejectActionFrom(ACTOR_ID, new SimulatedFailure("failureId", obj ->
        ((TestActor) obj).someAction(), new RuntimeException()));

    assertThat(rejections.size()).isEqualTo(1);
    assertThat(rejections.get(ACTOR_ID)).isNotNull();
  }

  @Test
  void removes_provided_failure_from_rejections() {
    Map<ActorId, Set<SimulatedFailure>> rejections = new HashMap<>();
    rejections.put(ACTOR_ID, ImmutableSet.of(new SimulatedFailure("failureId", mock ->
        ((TestActor) mock).someAction(), new RuntimeException())));

    TestActor actionPerformer = new TestActor();
    actionPerformer.acceptActionFrom(ACTOR_ID);

    assertThat(rejections.size()).as("acceptance has not been executed").isEqualTo(1);

    TestActor updatedActionPerformer = overrider.override(actionPerformer, rejections);

    updatedActionPerformer.acceptActionFrom(ACTOR_ID);

    assertThat(rejections).isEmpty();
  }

  @EqualsAndHashCode
  private static class TestActor implements ActorBase {
    void someAction() {
    }
  }
}