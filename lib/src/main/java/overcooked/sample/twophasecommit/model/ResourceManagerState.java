package overcooked.sample.twophasecommit.model;

import overcooked.sample.twophasecommit.modelverifier.SimpleResourceManagerClient;

/**
 * The state of a {@link SimpleResourceManagerClient}.
 */
public enum ResourceManagerState {
  WORKING,
  PREPARED,
  COMMITTED,
  ABORTED,
}
