package overcooked.sample.twophasecommit.modelverifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

class SimpleTransactionManagerClientTest {
  private static final String RESOURCE_MANAGER_0 = "0";
  private static final String RESOURCE_MANAGER_1 = "1";


  static Object[][] test_cases() {
    return new Object[][] {
        // current, action,         success, expected
        {WORKING,   Action.PREPARE, true,    PREPARED },
        {WORKING,   Action.ABORT,   false,   WORKING  },
        {PREPARED,  Action.PREPARE, true,    PREPARED },
        {PREPARED,  Action.ABORT,   true,    ABORTED  },
        {COMMITTED, Action.PREPARE, false,   COMMITTED},
        {COMMITTED, Action.ABORT,   false,   COMMITTED},
        {ABORTED,   Action.PREPARE, false,   ABORTED  },
        {ABORTED,   Action.ABORT,   true,    ABORTED  },
    };
  }

  @ParameterizedTest
  @MethodSource("test_cases")
  @SuppressWarnings({"checkstyle:CyclomaticComplexity"})
  void only_legal_state_transitions_are_allowed(ResourceManagerState currentState,
                                                Action action,
                                                boolean success,
                                                ResourceManagerState expectedState) {
    Map<String, ResourceManagerState> resourceManagerStates = new HashMap<>();
    resourceManagerStates.put(RESOURCE_MANAGER_0, currentState);
    resourceManagerStates.put(RESOURCE_MANAGER_1, currentState);
    SimpleTransactionManagerClient
        simpleTransactionManagerClient = new SimpleTransactionManagerClient(resourceManagerStates);

    if (success) {
      switch (action) {
        case PREPARE -> simpleTransactionManagerClient.prepare(RESOURCE_MANAGER_0);
        case ABORT -> simpleTransactionManagerClient.abort(RESOURCE_MANAGER_0);
        default -> throw new RuntimeException("Unexpected action: {}" + action);
      }
    } else {
      assertThatThrownBy(() -> {
        switch (action) {
          case PREPARE -> simpleTransactionManagerClient.prepare(RESOURCE_MANAGER_0);
          case ABORT -> simpleTransactionManagerClient.abort(RESOURCE_MANAGER_0);
          default -> throw new RuntimeException("Unexpected action: {}" + action);
        }
      }).isInstanceOf(IllegalStateException.class);
    }
    assertThat(simpleTransactionManagerClient.getResourceManagerStates())
        .isEqualTo(ImmutableMap.of(
            RESOURCE_MANAGER_0, expectedState,
            RESOURCE_MANAGER_1, currentState
        ));
  }

  @SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
      justification = "this is just a sample")
  private enum Action {
    PREPARE,
    ABORT,
  }
}