package overcooked.sample.twophasecommit.modelverifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.SimpleResourceManagerServer;

class ResourceManagerTest {
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

    ResourceManager resourceManager =
        new ResourceManager(resourceManagerServer);

    if (success) {
      doAction(resourceManager, action);
    } else {
      assertThatThrownBy(() -> doAction(resourceManager, action))
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
    SimpleResourceManagerServer resourceManagerServer =
        new SimpleResourceManagerServer(RESOURCE_MANAGER_ID, currentState);

    ResourceManager resourceManager =
        new ResourceManager(resourceManagerServer);

    TransactionManager transactionManager = mock(TransactionManager.class);

    if (success) {
      doAction(resourceManager, action, transactionManager);
    } else {
      assertThatThrownBy(() -> doAction(resourceManager, action, transactionManager))
          .isInstanceOf(IllegalStateException.class);
    }
    assertThat(resourceManagerServer.getState()).isEqualTo(expectedState);
  }

  private void doAction(ResourceManager resourceManager, Action action) {
    switch (action) {
      case COMMIT -> resourceManager.commit();
      case ABORT -> resourceManager.abort();
      default -> throw new RuntimeException("Unexpected new state: {}" + action);
    }
  }

  private void doAction(ResourceManager resourceManager,
                        Action action,
                        TransactionManager transactionManager) {
    switch (action) {
      case PREPARE -> {
        resourceManager.prepare(transactionManager);
        verify(transactionManager).prepare(RESOURCE_MANAGER_ID);
      }
      case ABORT -> {
        resourceManager.abort(transactionManager);
        verify(transactionManager).abort(RESOURCE_MANAGER_ID);
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