package overcooked.sample.twophasecommit.model;

/**
 * The state of a {@link ResourceManager}.
 */
public enum ResourceManagerState {
  WORKING,
  PREPARED,
  COMMITTED,
  ABORTED,
}
