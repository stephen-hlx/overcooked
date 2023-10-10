package overcooked.core.actor;

public interface ActorFactory<ActorType> {
    ActorType restoreFromLocalState(LocalState localState);
}
