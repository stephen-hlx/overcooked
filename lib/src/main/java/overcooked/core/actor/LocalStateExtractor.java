package overcooked.core.actor;

/**
 * Extracts the {@link LocalState} from an actor.
 */
public interface LocalStateExtractor<ActorT> {
  LocalState extract(ActorT actor);
}
