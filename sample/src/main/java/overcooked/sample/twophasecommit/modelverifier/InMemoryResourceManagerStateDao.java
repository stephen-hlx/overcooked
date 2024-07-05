package overcooked.sample.twophasecommit.modelverifier;

import lombok.AllArgsConstructor;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.ResourceManagerStateDao;

/**
 * An in memory implementation of {@link ResourceManagerStateDao}.
 */
@AllArgsConstructor
public class InMemoryResourceManagerStateDao implements ResourceManagerStateDao {
  private ResourceManagerState state;

  @Override
  public ResourceManagerState get() {
    return this.state;
  }

  @Override
  public void save(ResourceManagerState state) {
    this.state = state;
  }
}
