package overcooked.sample.twophasecommit.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SimpleResourceManagerServerTest {

  private static final String RESOURCE_MANAGER_0 = "0";

  static Object[][] test_cases() {
    return new Object[][] {
        // current,   action,         success, expected
        {  WORKING,   Action.PREPARE, true,    PREPARED },
        {  WORKING,   Action.ABORT,   false,   WORKING  },
        {  PREPARED,  Action.PREPARE, true,    PREPARED },
        {  PREPARED,  Action.ABORT,   true,    ABORTED  },
        {  COMMITTED, Action.PREPARE, false,   COMMITTED},
        {  COMMITTED, Action.ABORT,   false,   COMMITTED},
        {  ABORTED,   Action.PREPARE, false,   ABORTED  },
        {  ABORTED,   Action.ABORT,   true,    ABORTED  },
    };
  }

  @ParameterizedTest
  @MethodSource("test_cases")
  @SuppressWarnings({"checkstyle:CyclomaticComplexity"})
  void only_legal_state_transitions_are_allowed(ResourceManagerState currentState,
                                                Action action,
                                                boolean success,
                                                ResourceManagerState expectedState) {
    SimpleResourceManagerServer resourceManagerClient =
        new SimpleResourceManagerServer(RESOURCE_MANAGER_0, currentState);

    TransactionManagerClient transactionManagerClient =
        mock(TransactionManagerClient.class);

    if (success) {
      switch (action) {
        case PREPARE -> {
          resourceManagerClient.prepare(transactionManagerClient);
          verify(transactionManagerClient).prepare(RESOURCE_MANAGER_0);
        }
        case ABORT -> {
          resourceManagerClient.abort(transactionManagerClient);
          verify(transactionManagerClient).abort(RESOURCE_MANAGER_0);
        }
        default -> throw new RuntimeException("Unexpected action: {}" + action);
      }
    } else {
      assertThatThrownBy(() -> {
        switch (action) {
          case PREPARE -> resourceManagerClient.prepare(transactionManagerClient);
          case ABORT -> resourceManagerClient.abort(transactionManagerClient);
          default -> throw new RuntimeException("Unexpected action: {}" + action);
        }
      }).isInstanceOf(IllegalStateException.class);
    }
    assertThat(resourceManagerClient.getState())
        .isEqualTo(expectedState);
    verifyNoMoreInteractions(transactionManagerClient);
  }

  @SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
      justification = "this is just a sample")
  private enum Action {
    PREPARE,
    ABORT,
  }
}