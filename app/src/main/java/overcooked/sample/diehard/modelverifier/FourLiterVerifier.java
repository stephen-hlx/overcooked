package overcooked.sample.diehard.modelverifier;

import overcooked.core.GlobalState;
import overcooked.core.GlobalStateVerificationException;
import overcooked.core.GlobalStateVerifier;

public class FourLiterVerifier implements GlobalStateVerifier {
    @Override
    public void verify(GlobalState globalState) throws GlobalStateVerificationException {
        globalState.getLocalStates().values().forEach(localState -> {
            if (localState instanceof Jar5State) {
                if (((Jar5State) localState).getOccupancy() == 4) {
                    throw new GlobalStateVerificationException("Found it");
                }
            }
        });
    }
}
