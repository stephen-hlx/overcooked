package overcooked.core.actor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.function.Consumer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

/**
 * The simulation of a failure.
 * The ID in the struct is only meant to identify the simulated failure since `Consumer<>` is not
 * something with a meaningful implementation of `equals` or `hashCode`. And this is required to
 * ensure that the set of simulated failures do not have duplicates in it.
 */
@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"failureAction"})
@SuppressFBWarnings(value = { "EI_EXPOSE_REP" },
    justification = "this is just for internal use, making it immutable is over engineering")
public class SimulatedFailure {
  @EqualsAndHashCode.Include
  String id;
  Consumer<ActorBase> failureAction;
  RuntimeException runtimeException;
}
