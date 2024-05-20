package overcooked.core.actor;

/**
 * An actor that can kill itself and can also resurrect itself.
 * <p/>
 * This allows simulating an actor being unavailable and also coming back to
 * life.
 */
public interface ActorBase {

  default void rejectActionFrom(ActorId other, SimulatedFailure simulatedFailure) {
  }

  default void acceptActionFrom(ActorId other) {
  }
}
