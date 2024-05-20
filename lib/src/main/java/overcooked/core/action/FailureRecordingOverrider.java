package overcooked.core.action;

import static org.mockito.ArgumentMatchers.any;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.mockito.Mockito;
import overcooked.core.actor.ActorBase;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.SimulatedFailure;

/**
 * This class, via the use of Mockito's `spy` feature, overrides the
 * `ActorBase`'s default behaviour. This allows the capture and removal of
 * simulated failures for a specific actor in the collection provided.
 */
class FailureRecordingOverrider {
  <ActorT> ActorT override(ActorT actor, Map<ActorId, Set<SimulatedFailure>> rejections) {
    ActorT spied = Mockito.spy(actor);

    Mockito.doAnswer(invocation -> {
      ActorId providedActorId = invocation.getArgument(0);
      SimulatedFailure providedFailure = invocation.getArgument(1);
      rejections.computeIfAbsent(providedActorId, (notUsed) -> new HashSet<>())
          .add(providedFailure);
      return null;
    }).when((ActorBase) spied).rejectActionFrom(any(ActorId.class), any(SimulatedFailure.class));

    Mockito.doAnswer(invocation -> {
      ActorId providedActorId = invocation.getArgument(0);
      rejections.remove(providedActorId);
      return null;
    }).when((ActorBase) spied).acceptActionFrom(any(ActorId.class));

    return spied;
  }
}
