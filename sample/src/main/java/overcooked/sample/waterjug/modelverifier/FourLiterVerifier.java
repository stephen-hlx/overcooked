package overcooked.sample.waterjug.modelverifier;

import overcooked.core.GlobalState;
import overcooked.core.InvariantVerifier;
import overcooked.core.actor.ActorState;
import overcooked.core.actor.LocalState;

/**
 * The {@link InvariantVerifier} of the WaterJug sample.
 */
public class FourLiterVerifier implements InvariantVerifier {
  @Override
  public boolean verify(GlobalState globalState) {
    for (LocalState localState : globalState.getCopyOfLocalStates().values()) {
      ActorState actorState = localState.getActorState();
      if (actorState instanceof Jug5State) {
        Jug5State jug5State = (Jug5State) localState.getActorState();
        if (jug5State.getOccupancy() == 4) {
          return false;
        }
      }
    }
    return true;
  }
}
