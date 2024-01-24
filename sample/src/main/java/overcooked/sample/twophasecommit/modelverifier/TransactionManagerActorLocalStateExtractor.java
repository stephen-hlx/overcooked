package overcooked.sample.twophasecommit.modelverifier;

import overcooked.core.actor.LocalState;
import overcooked.core.actor.LocalStateExtractor;

class TransactionManagerActorLocalStateExtractor
    implements LocalStateExtractor<TransactionManagerActor> {
  @Override
  public LocalState extract(TransactionManagerActor transactionManagerActor) {
    return new TransactionManagerLocalState(transactionManagerActor.getStates().getData());
  }
}
