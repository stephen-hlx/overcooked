package overcooked.core.actor;

/**
 * Extracts the {@link LocalState} from an actor.
 */
public interface LocalStateExtractor {
  LocalState extract(Object actor);
}
