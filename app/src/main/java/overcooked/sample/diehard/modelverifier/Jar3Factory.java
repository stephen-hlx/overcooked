package overcooked.sample.diehard.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.LocalState;
import overcooked.sample.diehard.model.Jar3;

public class Jar3Factory implements ActorFactory<Jar3> {
    @Override
    public Jar3 restoreFromLocalState(LocalState localState) {
        Jar3State jar3State = (Jar3State) localState;
        return new Jar3(jar3State.getOccupancy());
    }
}
