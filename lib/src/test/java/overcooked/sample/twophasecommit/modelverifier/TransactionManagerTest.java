package overcooked.sample.twophasecommit.modelverifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.SimpleTransactionManagerServer;

class TransactionManagerTest {
  private static final String RESOURCE_MANAGER_ID = "0";

  static Object[][] passive_action_cases() {
    return new Object[][] {
        // current,   action,        success, expected
        {  WORKING,   Action.ABORT,   false,   WORKING  },
        {  WORKING,   Action.PREPARE, true,    PREPARED },
        {  PREPARED,  Action.ABORT,   true,    ABORTED  },
        {  PREPARED,  Action.PREPARE, true,    PREPARED },
        {  COMMITTED, Action.ABORT,   false,   COMMITTED},
        {  COMMITTED, Action.PREPARE, false,   COMMITTED},
        {  ABORTED,   Action.ABORT,   true,    ABORTED  },
        {  ABORTED,   Action.PREPARE, false,   ABORTED  },
    };
  }

  @ParameterizedTest
  @MethodSource("passive_action_cases")
  void passive_action_works(ResourceManagerState currentState,
                            Action action,
                            boolean success,
                            ResourceManagerState expectedState) {
    Map<String, ResourceManagerState> resourceManagerStates = new HashMap<>();
    resourceManagerStates.put(RESOURCE_MANAGER_ID, currentState);
    SimpleTransactionManagerServer transactionManagerServer =
        new SimpleTransactionManagerServer(resourceManagerStates);

    TransactionManager transactionManager = new TransactionManager(transactionManagerServer);

    if (success) {
      doAction(transactionManager, action);
    } else {
      assertThatThrownBy(() -> doAction(transactionManager, action))
          .isInstanceOf(IllegalStateException.class);
    }
    assertThat(transactionManagerServer.getResourceManagerStates())
        .isEqualTo(ImmutableMap.of(RESOURCE_MANAGER_ID, expectedState));
  }

  static Object[][] proactive_action_cases() {
    return new Object[][] {
        // current,   action,        success, expected
        {  WORKING,   Action.ABORT,  false,   WORKING  },
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
  @MethodSource("proactive_action_cases")
  void proactive_action_works(ResourceManagerState currentState,
                              Action action,
                              boolean success,
                              ResourceManagerState expectedState) {
    Map<String, ResourceManagerState> resourceManagerStates = new HashMap<>();
    resourceManagerStates.put(RESOURCE_MANAGER_ID, currentState);
    SimpleTransactionManagerServer transactionManagerServer =
        spy(new SimpleTransactionManagerServer(resourceManagerStates));

    TransactionManager transactionManager = new TransactionManager(transactionManagerServer);

    ResourceManager resourceManager = mock(ResourceManager.class);
    when(resourceManager.getId()).thenReturn(RESOURCE_MANAGER_ID);

    if (success) {
      doAction(transactionManager, action, resourceManager);
    } else {
      assertThatThrownBy(() -> doAction(transactionManager, action, resourceManager))
          .isInstanceOf(IllegalStateException.class);
    }
    assertThat(transactionManagerServer.getResourceManagerStates().get(RESOURCE_MANAGER_ID))
        .isEqualTo(expectedState);
  }

  private void doAction(TransactionManager transactionManager, Action action) {
    switch (action) {
      case ABORT -> transactionManager.abort(RESOURCE_MANAGER_ID);
      case PREPARE -> transactionManager.prepare(RESOURCE_MANAGER_ID);
      default -> throw new RuntimeException("Unexpected new state: {}" + action);
    }
  }

  private void doAction(TransactionManager transactionManager,
                        Action action,
                        ResourceManager resourceManager) {
    switch (action) {
      case ABORT -> transactionManager.abort(resourceManager);
      case COMMIT -> transactionManager.commit(resourceManager);
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
