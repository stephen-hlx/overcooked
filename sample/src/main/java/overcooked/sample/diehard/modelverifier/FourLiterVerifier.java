package overcooked.sample.diehard.modelverifier;

import overcooked.core.GlobalState;
import overcooked.core.InvariantVerifier;
import overcooked.core.actor.LocalState;

/**
 * The {@link InvariantVerifier} of the Jar sample.
 */
public class FourLiterVerifier implements InvariantVerifier {
  @Override
  public boolean verify(GlobalState globalState) {
    for (LocalState localState : globalState.getCopyOfLocalStates().values()) {
      if (localState instanceof Jar5State) {
        if (((Jar5State) localState).getOccupancy() == 4) {
          return false;
        }
      }
    }
    return true;
  }
}
