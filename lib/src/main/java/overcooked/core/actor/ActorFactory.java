package overcooked.core.actor;

/**
 * Creates an actor from its {@link LocalState}.
 *
 * @param <ActorT> the type of the actor
 */
public interface ActorFactory<ActorT> {
  ActorT restoreFromLocalState(LocalState localState);
}
