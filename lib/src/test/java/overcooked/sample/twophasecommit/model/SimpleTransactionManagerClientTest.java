package overcooked.sample.twophasecommit.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;
import static overcooked.sample.twophasecommit.model.SimpleTransactionManagerClientTest.Action.ABORT;
import static overcooked.sample.twophasecommit.model.SimpleTransactionManagerClientTest.Action.COMMIT;
import static overcooked.sample.twophasecommit.model.SimpleTransactionManagerClientTest.Action.PREPARE;

import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SimpleTransactionManagerClientTest {
  private static final int RESOURCE_MANAGER_0 = 0;
  private static final int RESOURCE_MANAGER_1 = 1;


  static Object[][] test_cases() {
    return new Object[][] {
        // current, action,  success, expected
        {WORKING,   PREPARE, true,    PREPARED },
        {WORKING,   COMMIT,  false,   WORKING  },
        {WORKING,   ABORT,   false,   WORKING  },
        {PREPARED,  ABORT,   true,    ABORTED  },
        {PREPARED,  PREPARE, true,    PREPARED },
        {PREPARED,  COMMIT,  true,    COMMITTED},
        {COMMITTED, PREPARE, false,   COMMITTED},
        {COMMITTED, COMMIT,  true,    COMMITTED},
        {COMMITTED, ABORT,   false,   COMMITTED},
        {ABORTED,   PREPARE, false,   ABORTED  },
        {ABORTED,   COMMIT,  false,   ABORTED  },
        {ABORTED,   ABORT,   true,    ABORTED  },
    };
  }

  @ParameterizedTest
  @MethodSource("test_cases")
  void only_legal_state_transitions_are_allowed(ResourceManagerState currentState,
                                                Action action,
                                                boolean success,
                                                ResourceManagerState expectedState) {
    Map<Integer, ResourceManagerState> resourceManagerStates = new HashMap<>();
    resourceManagerStates.put(RESOURCE_MANAGER_0, currentState);
    resourceManagerStates.put(RESOURCE_MANAGER_1, currentState);
    SimpleTransactionManagerClient
        simpleTransactionManagerClient = new SimpleTransactionManagerClient(resourceManagerStates);

    SimpleResourceManagerClient simpleResourceManagerClient =
        mock(SimpleResourceManagerClient.class);
    when(simpleResourceManagerClient.getId()).thenReturn(RESOURCE_MANAGER_0);

    if (success) {
      doAction(simpleTransactionManagerClient, action, simpleResourceManagerClient);
    } else {
      assertThatThrownBy(() ->
          doAction(simpleTransactionManagerClient, action, simpleResourceManagerClient))
          .isInstanceOf(IllegalStateException.class);
    }
    assertThat(simpleTransactionManagerClient.getResourceManagerStates())
        .isEqualTo(ImmutableMap.of(
            RESOURCE_MANAGER_0, expectedState,
            RESOURCE_MANAGER_1, currentState
        ));
    verify(simpleResourceManagerClient).getId();
    verifyNoMoreInteractions(simpleResourceManagerClient);
  }

  private static void doAction(SimpleTransactionManagerClient simpleTransactionManagerClient,
                               Action action,
                               SimpleResourceManagerClient simpleResourceManagerClient) {
    switch (action) {
      case PREPARE -> simpleTransactionManagerClient.prepare(simpleResourceManagerClient);
      case COMMIT -> {
        simpleTransactionManagerClient.commit(simpleResourceManagerClient);
        verify(simpleResourceManagerClient).commit();
      }
      case ABORT -> {
        simpleTransactionManagerClient.abort(simpleResourceManagerClient);
        verify(simpleResourceManagerClient).abort();
      }
      default -> throw new RuntimeException("Unexpected action: {}" + action);
    }
  }

  @SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
      justification = "this is just a sample")
  enum Action {
    PREPARE,
    COMMIT,
    ABORT,
  }
}