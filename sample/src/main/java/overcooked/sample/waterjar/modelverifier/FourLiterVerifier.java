package overcooked.sample.waterjar.modelverifier;

import overcooked.core.GlobalState;
import overcooked.core.InvariantVerifier;
import overcooked.core.actor.ActorState;

/**
 * The {@link InvariantVerifier} of the Jar sample.
 */
public class FourLiterVerifier implements InvariantVerifier {
  @Override
  public boolean verify(GlobalState globalState) {
    for (ActorState actorState : globalState.getCopyOfLocalStates().values()) {
      if (actorState instanceof Jar5State) {
        if (((Jar5State) actorState).getOccupancy() == 4) {
          return false;
        }
      }
    }
    return true;
  }
}
