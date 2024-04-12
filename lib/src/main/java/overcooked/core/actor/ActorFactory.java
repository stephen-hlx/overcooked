package overcooked.core.actor;

/**
 * Creates an actor from its {@link ActorState}.
 *
 * @param <ActorT> the type of the actor
 */
public interface ActorFactory<ActorT> {
  ActorT restoreFromActorState(ActorState actorState);
}
