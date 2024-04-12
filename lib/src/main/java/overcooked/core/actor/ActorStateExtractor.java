package overcooked.core.actor;

/**
 * Extracts the {@link ActorState} from an actor.
 */
public interface ActorStateExtractor<ActorT> {
  ActorState extract(ActorT actor);
}
