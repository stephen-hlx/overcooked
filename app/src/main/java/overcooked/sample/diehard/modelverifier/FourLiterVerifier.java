package overcooked.sample.diehard.modelverifier;

import overcooked.core.GlobalState;
import overcooked.core.GlobalStateVerifier;
import overcooked.core.actor.LocalState;

public class FourLiterVerifier implements GlobalStateVerifier {
    @Override
    public boolean validate(GlobalState globalState) {
        for (LocalState localState : globalState.getLocalStates().values()) {
            if (localState instanceof Jar5State) {
                if (((Jar5State) localState).getOccupancy() == 4) {
                    return false;
                }
            }
        }
        return true;
    }
}
