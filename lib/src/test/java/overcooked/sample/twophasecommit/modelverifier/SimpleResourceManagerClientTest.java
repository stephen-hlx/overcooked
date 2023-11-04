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
import overcooked.sample.twophasecommit.model.ResourceManagerState;

class SimpleResourceManagerClientTest {
  private static final String RESOURCE_MANAGER_ID = "0";

  static Object[][] test_cases() {
    return new Object[][] {
        // current,   action,        success, expected
        {  WORKING,   Action.COMMIT, false,   WORKING  },
        {  WORKING,   Action.ABORT,  false,   WORKING  },
        {  PREPARED,  Action.ABORT,  true,    ABORTED  },
        {  PREPARED,  Action.COMMIT, true,    COMMITTED},
        {  COMMITTED, Action.COMMIT, true,    COMMITTED},
        {  COMMITTED, Action.ABORT,  false,   COMMITTED},
        {  ABORTED,   Action.COMMIT, false,   ABORTED  },
        {  ABORTED,   Action.ABORT,  true,    ABORTED  },
    };
  }

  @ParameterizedTest
  @MethodSource("test_cases")
  void only_legal_state_transitions_are_allowed(ResourceManagerState currentState,
                                                Action action,
                                                boolean success,
                                                ResourceManagerState expectedState) {
    SimpleResourceManagerClient resourceManagerClient =
        new SimpleResourceManagerClient(RESOURCE_MANAGER_ID,
            currentState);

    if (success) {
      doAction(resourceManagerClient, action);
    } else {
      assertThatThrownBy(() -> doAction(resourceManagerClient, action))
          .isInstanceOf(IllegalStateException.class);
    }
    assertThat(resourceManagerClient.getState()).isEqualTo(expectedState);
  }

  private void doAction(SimpleResourceManagerClient simpleResourceManagerClient, Action action) {
    switch (action) {
      case COMMIT -> simpleResourceManagerClient.commit();
      case ABORT -> simpleResourceManagerClient.abort();
      default -> throw new RuntimeException("Unexpected new state: {}" + action);
    }
  }

  @SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
      justification = "this is just a sample")
  private enum Action {
    COMMIT,
    ABORT,
  }
}