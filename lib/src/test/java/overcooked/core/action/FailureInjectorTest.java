package overcooked.core.action;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import overcooked.core.actor.ActorBase;
import overcooked.core.actor.SimulatedFailure;

class FailureInjectorTest {
  private final FailureInjector injector = new FailureInjector();

  @Test
  void injects_failures() {
    TestActor actor = new TestActor();
    actor.someAction();

    TestActor injected = injector.inject(actor, ImmutableSet.of(
        new SimulatedFailure("someId",
            mock -> ((TestActor) mock).someAction(),
            new RuntimeException())
    ));

    assertThatThrownBy(injected::someAction).isInstanceOf(RuntimeException.class);
  }

  @Test
  void with_null_simulated_failure_set_no_failure_is_injected() {
    TestActor actor = new TestActor();
    actor.someAction();

    TestActor injected = injector.inject(actor, null);

    injected.someAction();
  }

  @EqualsAndHashCode
  private static class TestActor implements ActorBase {
    void someAction() {
    }
  }
}