package overcooked.sample.twophasecommit.modelverifier;

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
import overcooked.sample.twophasecommit.model.ResourceManagerServer;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.TransactionManagerClient;

class InMemoryResourceManagerServerTest {

  private static final String RESOURCE_MANAGER_ID = "0";

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
    RefCell<ResourceManagerState> stateRefCell = new RefCell<>(currentState);
    InMemoryResourceManagerServer resourceManager =
        new InMemoryResourceManagerServer(RESOURCE_MANAGER_ID, stateRefCell);

    TransactionManagerClient transactionManagerClient = mock(TransactionManagerClient.class);

    if (success) {
      doAction(resourceManager, action, transactionManagerClient);
    } else {
      assertThatThrownBy(() -> doAction(resourceManager, action, transactionManagerClient))
          .isInstanceOf(IllegalStateException.class);
    }
    assertThat(stateRefCell.getData()).isEqualTo(expectedState);
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
    PREPARE,
  }
}