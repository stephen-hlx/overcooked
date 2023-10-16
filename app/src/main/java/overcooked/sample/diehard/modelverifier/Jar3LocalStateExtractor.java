package overcooked.sample.diehard.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;
import overcooked.sample.diehard.model.Jar3;

public class Jar3LocalStateExtractor implements LocalStateExtractor {
    @Override
    public LocalState extract(Object actor) {
        Jar3 jar3 = (Jar3) actor;
        return new Jar3State(jar3.getOccupancy());
    }
}
