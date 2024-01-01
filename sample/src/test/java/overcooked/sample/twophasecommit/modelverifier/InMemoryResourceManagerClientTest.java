package overcooked.sample.twophasecommit.modelverifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

class InMemoryResourceManagerClientTest {

  private static final String RESOURCE_MANAGER_ID = "0";

  static Object[][] test_cases() {
    return new Object[][] {
        // current,   action,        success, expected
        {  WORKING,   Action.ABORT,  true,    ABORTED  },
        {  WORKING,   Action.COMMIT, false,   WORKING  },
        {  PREPARED,  Action.ABORT,  true,    ABORTED  },
        {  PREPARED,  Action.COMMIT, true,    COMMITTED},
        {  COMMITTED, Action.ABORT,  false,   COMMITTED},
        {  COMMITTED, Action.COMMIT, true,    COMMITTED},
        {  ABORTED,   Action.ABORT,  true,    ABORTED  },
        {  ABORTED,   Action.COMMIT, false,   ABORTED  },
    };
  }

  @ParameterizedTest
  @MethodSource("test_cases")
  void works(ResourceManagerState currentState,
             Action action,
             boolean success,
             ResourceManagerState expectedState) {
    RefCell<ResourceManagerState> stateRefCell = new RefCell<>(currentState);
    InMemoryResourceManagerClient resourceManagerClient =
        new InMemoryResourceManagerClient(RESOURCE_MANAGER_ID, stateRefCell);

    if (success) {
      doAction(resourceManagerClient, action);
    } else {
      assertThatThrownBy(() -> doAction(resourceManagerClient, action))
          .isInstanceOf(IllegalStateException.class);
    }
    assertThat(stateRefCell.getData()).isEqualTo(expectedState);
  }

  private void doAction(ResourceManagerClient resourceManagerClient, Action action) {
    switch (action) {
      case COMMIT -> resourceManagerClient.commit();
      case ABORT -> resourceManagerClient.abort();
      default -> throw new RuntimeException("Unexpected new state: {}" + action);
    }
  }

  @SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
      justification = "this is just a sample")
  private enum Action {
    ABORT,
    COMMIT,
  }
}