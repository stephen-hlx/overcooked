package overcooked.sample.twophasecommit.model;

/**
 * The DAO for storing {@link ResourceManagerState}.
 */
public interface ResourceManagerStateDao {
  /**
   * Retrieve the {@link ResourceManagerState}.
   *
   * @return the {@link ResourceManagerState}
   */
  ResourceManagerState get();

  /**
   * Persists the {@link ResourceManagerState}.
   */
  void save(ResourceManagerState state);
}
