package overcooked.sample.twophasecommit.modelverifier;

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
import overcooked.sample.twophasecommit.model.ResourceManagerClient;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

class TransactionManagerTest {

  private static final String RESOURCE_MANAGER_0 = "0";
  private static final String RESOURCE_MANAGER_1 = "1";

  static Object[][] passive_action_cases() {
    return new Object[][] {
        // current,                                       expected
        // RM0_state, RM1_state, actionByRm1,    success, RM0_state, RM1_state,
        {  WORKING,   WORKING,   Action.PREPARE, true,    WORKING,   PREPARED },
        {  WORKING,   WORKING,   Action.ABORT,   true,    WORKING,   ABORTED  },
        {  WORKING,   PREPARED,  Action.PREPARE, true,    WORKING,   PREPARED },
        {  WORKING,   PREPARED,  Action.ABORT,   false,   WORKING,   PREPARED },
        {  WORKING,   COMMITTED, Action.PREPARE, false,   WORKING,   COMMITTED},
        {  WORKING,   COMMITTED, Action.ABORT,   false,   WORKING,   COMMITTED},
        {  WORKING,   ABORTED,   Action.PREPARE, false,   WORKING,   ABORTED  },
        {  WORKING,   ABORTED,   Action.ABORT,   true,    WORKING,   ABORTED  },

        {  PREPARED,  WORKING,   Action.PREPARE, true,    PREPARED,  PREPARED },
        {  PREPARED,  WORKING,   Action.ABORT,   true,    PREPARED,  ABORTED  },
        {  PREPARED,  PREPARED,  Action.PREPARE, true,    PREPARED,  PREPARED },
        {  PREPARED,  PREPARED,  Action.ABORT,   false,   PREPARED,  PREPARED },
        {  PREPARED,  COMMITTED, Action.PREPARE, false,   PREPARED,  COMMITTED},
        {  PREPARED,  COMMITTED, Action.ABORT,   false,   PREPARED,  COMMITTED},
        {  PREPARED,  ABORTED,   Action.PREPARE, false,   PREPARED,  ABORTED  },
        {  PREPARED,  ABORTED,   Action.ABORT,   true,    PREPARED,  ABORTED  },

        {  COMMITTED, WORKING,   Action.PREPARE, false,   COMMITTED, WORKING  },
        {  COMMITTED, WORKING,   Action.ABORT,   false,   COMMITTED, WORKING  },
        {  COMMITTED, PREPARED,  Action.PREPARE, true,    COMMITTED, PREPARED },
        {  COMMITTED, PREPARED,  Action.ABORT,   false,   COMMITTED, PREPARED },
        {  COMMITTED, COMMITTED, Action.PREPARE, false,   COMMITTED, COMMITTED},
        {  COMMITTED, COMMITTED, Action.ABORT,   false,   COMMITTED, COMMITTED},
        {  COMMITTED, ABORTED,   Action.PREPARE, false,   COMMITTED, ABORTED  },
        {  COMMITTED, ABORTED,   Action.ABORT,   false,   COMMITTED, ABORTED  },

        {  ABORTED,   WORKING,   Action.PREPARE, true,    ABORTED,   PREPARED },
        {  ABORTED,   WORKING,   Action.ABORT,   true,    ABORTED,   ABORTED  },
        {  ABORTED,   PREPARED,  Action.PREPARE, true,    ABORTED,   PREPARED },
        {  ABORTED,   PREPARED,  Action.ABORT,   false,   ABORTED,   PREPARED },
        {  ABORTED,   COMMITTED, Action.PREPARE, false,   ABORTED,   COMMITTED},
        {  ABORTED,   COMMITTED, Action.ABORT,   false,   ABORTED,   COMMITTED},
        {  ABORTED,   ABORTED,   Action.PREPARE, false,   ABORTED,   ABORTED  },
        {  ABORTED,   ABORTED,   Action.ABORT,   true,    ABORTED,   ABORTED  },
    };
  }

  @ParameterizedTest
  @MethodSource("passive_action_cases")
  void passive_action_works(ResourceManagerState rm0State,
                            ResourceManagerState rm1State,
                            Action action,
                            boolean success,
                            ResourceManagerState expectedRm0State,
                            ResourceManagerState expectedRm1State) {
    Map<String, ResourceManagerState> resourceManagerStates = new HashMap<>();
    resourceManagerStates.put(RESOURCE_MANAGER_0, rm0State);
    resourceManagerStates.put(RESOURCE_MANAGER_1, rm1State);
    RefCell<Map<String, ResourceManagerState>> stateRefCell = new RefCell<>(resourceManagerStates);

    TransactionManager transactionManager = new TransactionManager(stateRefCell);

    if (success) {
      doAction(transactionManager, action);
    } else {
      assertThatThrownBy(() -> doAction(transactionManager, action))
          .isInstanceOf(IllegalStateException.class);
    }
    assertThat(stateRefCell.getData()).isEqualTo(ImmutableMap.of(
            RESOURCE_MANAGER_0, expectedRm0State,
            RESOURCE_MANAGER_1, expectedRm1State));
  }

  static Object[][] proactive_action_cases() {
    return new Object[][] {
        // current,                                      expected
        // RM0_state, RM1_state, actionOnRm1,   success, RM0_state, RM1_state,
        {  WORKING,   WORKING,   Action.COMMIT, false,   WORKING,   WORKING  },
        {  WORKING,   WORKING,   Action.ABORT,  true,    WORKING,   ABORTED  },
        {  WORKING,   PREPARED,  Action.COMMIT, false,   WORKING,   PREPARED },
        {  WORKING,   PREPARED,  Action.ABORT,  true,    WORKING,   ABORTED  },
        {  WORKING,   COMMITTED, Action.COMMIT, false,   WORKING,   COMMITTED},
        {  WORKING,   COMMITTED, Action.ABORT,  false,   WORKING,   COMMITTED},
        {  WORKING,   ABORTED,   Action.COMMIT, false,   WORKING,   ABORTED  },
        {  WORKING,   ABORTED,   Action.ABORT,  true,    WORKING,   ABORTED  },

        {  PREPARED,  WORKING,   Action.COMMIT, false,   PREPARED,  WORKING  },
        {  PREPARED,  WORKING,   Action.ABORT,  true,    PREPARED,  ABORTED  },
        {  PREPARED,  PREPARED,  Action.COMMIT, true,    PREPARED,  COMMITTED},
        {  PREPARED,  PREPARED,  Action.ABORT,  true,    PREPARED,  ABORTED  },
        {  PREPARED,  COMMITTED, Action.COMMIT, true,    PREPARED,  COMMITTED},
        {  PREPARED,  COMMITTED, Action.ABORT,  false,   PREPARED,  COMMITTED},
        {  PREPARED,  ABORTED,   Action.COMMIT, false,   PREPARED,  ABORTED  },
        {  PREPARED,  ABORTED,   Action.ABORT,  true,    PREPARED,  ABORTED  },

        {  COMMITTED, WORKING,   Action.COMMIT, false,   COMMITTED, WORKING  },
        {  COMMITTED, WORKING,   Action.ABORT,  false,   COMMITTED, WORKING  },
        {  COMMITTED, PREPARED,  Action.COMMIT, true,    COMMITTED, COMMITTED},
        {  COMMITTED, PREPARED,  Action.ABORT,  false,   COMMITTED, PREPARED },
        {  COMMITTED, COMMITTED, Action.COMMIT, true,    COMMITTED, COMMITTED},
        {  COMMITTED, COMMITTED, Action.ABORT,  false,   COMMITTED, COMMITTED},
        {  COMMITTED, ABORTED,   Action.COMMIT, false,   COMMITTED, ABORTED  },
        {  COMMITTED, ABORTED,   Action.ABORT,  false,   COMMITTED, ABORTED  },

        {  ABORTED,   WORKING,   Action.COMMIT, false,   ABORTED,   WORKING  },
        {  ABORTED,   WORKING,   Action.ABORT,  true,    ABORTED,   ABORTED  },
        {  ABORTED,   PREPARED,  Action.COMMIT, false,   ABORTED,   PREPARED },
        {  ABORTED,   PREPARED,  Action.ABORT,  true,    ABORTED,   ABORTED  },
        {  ABORTED,   COMMITTED, Action.COMMIT, false,   ABORTED,   COMMITTED},
        {  ABORTED,   COMMITTED, Action.ABORT,  false,   ABORTED,   COMMITTED},
        {  ABORTED,   ABORTED,   Action.COMMIT, false,   ABORTED,   ABORTED  },
        {  ABORTED,   ABORTED,   Action.ABORT,  true,    ABORTED,   ABORTED  },
    };
  }

  @ParameterizedTest
  @MethodSource("proactive_action_cases")
  @SuppressWarnings({"checkstyle:CyclomaticComplexity"})
  void proactive_action_works(ResourceManagerState rm0State,
                              ResourceManagerState rm1State,
                              Action action,
                              boolean success,
                              ResourceManagerState expectedRm0State,
                              ResourceManagerState expectedRm1State) {
    Map<String, ResourceManagerState> resourceManagerStates = new HashMap<>();
    resourceManagerStates.put(RESOURCE_MANAGER_0, rm0State);
    resourceManagerStates.put(RESOURCE_MANAGER_1, rm1State);
    RefCell<Map<String, ResourceManagerState>> stateRefCell = new RefCell<>(resourceManagerStates);

    TransactionManager transactionManager = new TransactionManager(stateRefCell);

    ResourceManagerClient rm1 = mock(ResourceManagerClient.class);
    when(rm1.getId()).thenReturn(RESOURCE_MANAGER_1);

    if (success) {
      switch (action) {
        case COMMIT -> {
          transactionManager.commit(rm1);
          verify(rm1).commit();
          verify(rm1).getId();
        }
        case ABORT -> {
          transactionManager.abort(rm1);
          verify(rm1).abort();
          verify(rm1).getId();
        }
        default -> throw new RuntimeException("Unexpected action: {}" + action);
      }
    } else {
      assertThatThrownBy(() -> {
        switch (action) {
          case COMMIT -> transactionManager.commit(rm1);
          case ABORT -> transactionManager.abort(rm1);
          default -> throw new RuntimeException("Unexpected action: {}" + action);
        }
      }).isInstanceOf(IllegalStateException.class);
    }
    assertThat(stateRefCell.getData()).isEqualTo(ImmutableMap.of(
        RESOURCE_MANAGER_0, expectedRm0State,
        RESOURCE_MANAGER_1, expectedRm1State));
    verifyNoMoreInteractions(rm1);
  }

  private void doAction(TransactionManager transactionManager, Action action) {
    switch (action) {
      case ABORT -> transactionManager.abort(RESOURCE_MANAGER_1);
      case PREPARE -> transactionManager.prepare(RESOURCE_MANAGER_1);
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
