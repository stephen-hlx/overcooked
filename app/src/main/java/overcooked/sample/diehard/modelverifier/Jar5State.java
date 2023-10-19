package overcooked.sample.diehard.modelverifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import overcooked.core.actor.LocalState;

@RequiredArgsConstructor
@Getter
@Value
public class Jar5State implements LocalState {
    int occupancy;

    public String toString() {
        return "occupancy=" + occupancy;
    }
}
