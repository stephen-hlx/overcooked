package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateExtractor;

class TransactionManagerActorStateExtractor
    implements ActorStateExtractor<TransactionManagerActor> {
  @Override
  public ActorState extract(TransactionManagerActor transactionManagerActor) {
    return new TransactionManagerActorState(transactionManagerActor.getResourceManagerStates());
  }
}
