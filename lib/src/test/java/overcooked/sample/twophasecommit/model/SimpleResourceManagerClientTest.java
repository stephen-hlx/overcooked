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
import static overcooked.sample.twophasecommit.model.SimpleTransactionManagerClientTest.Action.ABORT;
import static overcooked.sample.twophasecommit.model.SimpleTransactionManagerClientTest.Action.COMMIT;
import static overcooked.sample.twophasecommit.model.SimpleTransactionManagerClientTest.Action.PREPARE;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import overcooked.sample.twophasecommit.model.SimpleTransactionManagerClientTest.Action;

class SimpleResourceManagerClientTest {
  private static final int RESOURCE_MANAGER_ID = 0;

  private final SimpleTransactionManagerClient
      simpleTransactionManagerClient = mock(SimpleTransactionManagerClient.class);

  static Object[][] test_cases() {
    return new Object[][] {
        // current,   action,        success, expected
        {WORKING, PREPARE, true, PREPARED},
        {WORKING, COMMIT, false, WORKING},
        {WORKING, ABORT, false, WORKING},
        {PREPARED, ABORT, true, ABORTED},
        {PREPARED, PREPARE, true, PREPARED},
        {PREPARED, COMMIT, true, COMMITTED},
        {COMMITTED, PREPARE, false, COMMITTED},
        {COMMITTED, COMMIT, true, COMMITTED},
        {COMMITTED, ABORT, false, COMMITTED},
        {ABORTED, PREPARE, false, ABORTED},
        {ABORTED, COMMIT, false, ABORTED},
        {ABORTED, ABORT, true, ABORTED},
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
            simpleTransactionManagerClient,
            currentState);

    if (success) {
      doAction(resourceManagerClient, action);
    } else {
      assertThatThrownBy(() -> doAction(resourceManagerClient, action))
          .isInstanceOf(IllegalStateException.class);
    }
    assertThat(resourceManagerClient.getState()).isEqualTo(expectedState);
    verifyNoMoreInteractions(simpleTransactionManagerClient);
  }

  private void doAction(SimpleResourceManagerClient simpleResourceManagerClient, Action action) {
    switch (action) {
      case PREPARE -> {
        simpleResourceManagerClient.prepare();
        verify(simpleTransactionManagerClient).prepare(simpleResourceManagerClient);
      }
      case COMMIT -> simpleResourceManagerClient.commit();
      case ABORT -> simpleResourceManagerClient.abort();
      default -> throw new RuntimeException("Unexpected new state: {}" + action);
    }
  }
}