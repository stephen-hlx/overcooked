package overcooked.sample.diehard.modelverifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import overcooked.core.actor.LocalState;

@RequiredArgsConstructor
@Getter
@Value
public class Jar3State implements LocalState {
    int occupancy;
}
