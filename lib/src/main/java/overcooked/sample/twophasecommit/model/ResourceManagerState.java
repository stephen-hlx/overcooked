package overcooked.sample.twophasecommit.model;

/**
 * The state of a {@link SimpleResourceManagerClient}.
 */
public enum ResourceManagerState {
  WORKING,
  PREPARED,
  COMMITTED,
  ABORTED,
}
