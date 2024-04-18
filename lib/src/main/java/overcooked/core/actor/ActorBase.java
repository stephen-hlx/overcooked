package overcooked.core.actor;

/**
 * An actor that can kill itself and can also resurrect itself.
 * <p/>
 * This allows simulating an actor being unavailable and also coming back to
 * life.
 */
public interface ActorBase {

  /**
   * How this works needs a bit more thought.
   * Because when an actor dies, all communication to it should fail as well.
   * But without explicitly specifying an exception, it is not clear what the
   * behaviour will be.
   */
  default void suicide() {
  }

  default void resurrect() {
  }

  default void rejectActionFrom(ActorId other, RuntimeException runtimeException) {
  }

  default void acceptActionFrom(ActorId other) {
  }
}
