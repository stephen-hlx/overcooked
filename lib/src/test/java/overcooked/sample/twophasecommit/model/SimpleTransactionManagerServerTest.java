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

import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SimpleTransactionManagerServerTest {

  private static final String RESOURCE_MANAGER_0 = "0";
  private static final String RESOURCE_MANAGER_1 = "1";


  static Object[][] test_cases() {
    return new Object[][] {
        // current,   action,        success, expected
        {  WORKING,   Action.COMMIT, false,   WORKING  },
        {  WORKING,   Action.ABORT,  false,   WORKING  },
        {  PREPARED,  Action.COMMIT, true,    COMMITTED},
        {  PREPARED,  Action.ABORT,  true,    ABORTED  },
        {  COMMITTED, Action.COMMIT, true,    COMMITTED},
        {  COMMITTED, Action.ABORT,  false,   COMMITTED},
        {  ABORTED,   Action.COMMIT, false,   ABORTED  },
        {  ABORTED,   Action.ABORT,  true,    ABORTED  },
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
    SimpleTransactionManagerServer
        simpleTransactionManagerServer = new SimpleTransactionManagerServer(resourceManagerStates);

    ResourceManagerClient resourceManagerClient =
        mock(ResourceManagerClient.class);
    when(resourceManagerClient.getId()).thenReturn(RESOURCE_MANAGER_0);

    if (success) {
      switch (action) {
        case COMMIT -> {
          simpleTransactionManagerServer.commit(resourceManagerClient);
          verify(resourceManagerClient).commit();
        }
        case ABORT -> {
          simpleTransactionManagerServer.abort(resourceManagerClient);
          verify(resourceManagerClient).abort();
        }
        default -> throw new RuntimeException("Unexpected action: {}" + action);
      }
    } else {
      assertThatThrownBy(() -> {
        switch (action) {
          case COMMIT -> simpleTransactionManagerServer.commit(resourceManagerClient);
          case ABORT -> simpleTransactionManagerServer.abort(resourceManagerClient);
          default -> throw new RuntimeException("Unexpected action: {}" + action);
        }
      }).isInstanceOf(IllegalStateException.class);
    }
    assertThat(simpleTransactionManagerServer.getResourceManagerStates())
        .isEqualTo(ImmutableMap.of(
            RESOURCE_MANAGER_0, expectedState,
            RESOURCE_MANAGER_1, currentState
        ));
    verify(resourceManagerClient).getId();
    verifyNoMoreInteractions(resourceManagerClient);
  }

  @SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
      justification = "this is just a sample")
  private enum Action {
    COMMIT,
    ABORT,
  }
}