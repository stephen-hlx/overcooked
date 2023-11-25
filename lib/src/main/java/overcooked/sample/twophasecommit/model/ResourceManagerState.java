package overcooked.sample.twophasecommit.model;

/**
 * The state of a {@link ResourceManagerServer}.
 */
public enum ResourceManagerState {
  WORKING,
  PREPARED,
  COMMITTED,
  ABORTED,
}
