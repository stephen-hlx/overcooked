package overcooked.sample.twophasecommit.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class SimpleResourceManagerServerTest {

  private static final String RESOURCE_MANAGER_ID = "0";

  static Object[][] passive_action_cases() {
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
  @MethodSource("passive_action_cases")
  void passive_action_works(ResourceManagerState currentState,
                            Action action,
                            boolean success,
                            ResourceManagerState expectedState) {
    SimpleResourceManagerServer resourceManagerServer =
        new SimpleResourceManagerServer(RESOURCE_MANAGER_ID, currentState);

    if (success) {
      doAction(resourceManagerServer, action);
    } else {
      assertThatThrownBy(() -> doAction(resourceManagerServer, action))
          .isInstanceOf(IllegalStateException.class);
    }
    assertThat(resourceManagerServer.getState()).isEqualTo(expectedState);
  }

  static Object[][] proactive_action_cases() {
    return new Object[][] {
        // current,   action,         success, expected
        {  WORKING,   Action.ABORT,   true,    ABORTED  },
        {  WORKING,   Action.PREPARE, true,    PREPARED },
        {  PREPARED,  Action.ABORT,   false,   PREPARED },
        {  PREPARED,  Action.PREPARE, true,    PREPARED },
        {  COMMITTED, Action.ABORT,   false,   COMMITTED},
        {  COMMITTED, Action.PREPARE, false,   COMMITTED},
        {  ABORTED,   Action.ABORT,   false,   ABORTED  },
        {  ABORTED,   Action.PREPARE, false,   ABORTED  },
    };
  }

  @ParameterizedTest
  @MethodSource("proactive_action_cases")
  void proactive_action_works(ResourceManagerState currentState,
                              Action action,
                              boolean success,
                              ResourceManagerState expectedState) {
    SimpleResourceManagerServer resourceManager =
        new SimpleResourceManagerServer(RESOURCE_MANAGER_ID, currentState);

    TransactionManagerClient transactionManagerClient = mock(TransactionManagerClient.class);

    if (success) {
      doAction(resourceManager, action, transactionManagerClient);
    } else {
      assertThatThrownBy(() -> doAction(resourceManager, action, transactionManagerClient))
          .isInstanceOf(IllegalStateException.class);
    }
    assertThat(resourceManager.getState()).isEqualTo(expectedState);
  }

  private void doAction(ResourceManagerServer resourceManagerServer, Action action) {
    switch (action) {
      case COMMIT -> resourceManagerServer.commit();
      case ABORT -> resourceManagerServer.abort();
      default -> throw new RuntimeException("Unexpected new state: {}" + action);
    }
  }

  private void doAction(ResourceManagerServer resourceManagerServer,
                        Action action,
                        TransactionManagerClient transactionManagerClient) {
    switch (action) {
      case PREPARE -> {
        resourceManagerServer.prepare(transactionManagerClient);
        Mockito.verify(transactionManagerClient).prepare(RESOURCE_MANAGER_ID);
      }
      case ABORT -> {
        resourceManagerServer.abort(transactionManagerClient);
        Mockito.verify(transactionManagerClient).abort(RESOURCE_MANAGER_ID);
      }
      default -> throw new RuntimeException("Unexpected new state: {}" + action);
    }
  }

  @SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
      justification = "this is just a sample")
  private enum Action {
    ABORT,
    COMMIT,
    PREPARE,
  }
}