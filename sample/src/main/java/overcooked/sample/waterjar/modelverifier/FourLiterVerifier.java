package overcooked.sample.waterjar.modelverifier;

import overcooked.core.GlobalState;
import overcooked.core.InvariantVerifier;
import overcooked.core.actor.ActorState;
import overcooked.core.actor.LocalState;

/**
 * The {@link InvariantVerifier} of the Jar sample.
 */
public class FourLiterVerifier implements InvariantVerifier {
  @Override
  public boolean verify(GlobalState globalState) {
    for (LocalState localState : globalState.getCopyOfLocalStates().values()) {
      ActorState actorState = localState.getActorState();
      if (actorState instanceof Jar5State) {
        Jar5State jar5State = (Jar5State) localState.getActorState();
        if (jar5State.getOccupancy() == 4) {
          return false;
        }
      }
    }
    return true;
  }
}
