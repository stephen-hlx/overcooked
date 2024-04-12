package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.ActorFactory;
import overcooked.core.actor.ActorState;

class TransactionManagerFactory implements ActorFactory<TransactionManagerActor> {
  @Override
  public TransactionManagerActor restoreFromActorState(ActorState actorState) {
    TransactionManagerActorState state = (TransactionManagerActorState) actorState;
    return new TransactionManagerActor(state.getResourceManagerStates());
  }
}
