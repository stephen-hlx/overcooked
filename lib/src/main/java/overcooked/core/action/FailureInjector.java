package overcooked.core.action;

import java.util.Set;
import org.mockito.Mockito;
import overcooked.core.actor.ActorBase;
import overcooked.core.actor.SimulatedFailure;

class FailureInjector {
  <ActorT> ActorT inject(ActorT actor, Set<SimulatedFailure> failures) {
    if (failures == null) {
      return actor;
    }

    ActorT spied = Mockito.spy(actor);

    failures.forEach(failure -> {
      ActorT stub = Mockito.doThrow(failure.getRuntimeException()).when(spied);
      failure.getFailureAction().accept((ActorBase) stub);
    });

    return spied;
  }
}
