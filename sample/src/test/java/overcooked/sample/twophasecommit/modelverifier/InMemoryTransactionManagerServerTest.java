package overcooked.sample.twophasecommit.modelverifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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

class InMemoryTransactionManagerServerTest {

  private static final String RESOURCE_MANAGER_0 = "0";
  private static final String RESOURCE_MANAGER_1 = "1";

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
  void only_legal_state_transitions_are_allowed(ResourceManagerState rm0State,
                                                ResourceManagerState rm1State,
                                                Action action,
                                                boolean success,
                                                ResourceManagerState expectedRm0State,
                                                ResourceManagerState expectedRm1State) {
    Map<String, ResourceManagerState> resourceManagerStates = new HashMap<>();
    resourceManagerStates.put(RESOURCE_MANAGER_0, rm0State);
    resourceManagerStates.put(RESOURCE_MANAGER_1, rm1State);
    RefCell<Map<String, ResourceManagerState>> resourceManagerStatesRefCell =
        new RefCell<>(resourceManagerStates);
    InMemoryTransactionManagerServer inMemoryTransactionManagerServer =
        new InMemoryTransactionManagerServer(resourceManagerStatesRefCell);

    ResourceManagerClient rm1 = mock(ResourceManagerClient.class);
    when(rm1.getId()).thenReturn(RESOURCE_MANAGER_1);

    if (success) {
      switch (action) {
        case COMMIT -> {
          inMemoryTransactionManagerServer.commit(rm1);
          verify(rm1).commit();
        }
        case ABORT -> {
          inMemoryTransactionManagerServer.abort(rm1);
          verify(rm1).abort();
        }
        default -> throw new RuntimeException("Unexpected action: {}" + action);
      }
    } else {
      assertThatThrownBy(() -> {
        switch (action) {
          case COMMIT -> inMemoryTransactionManagerServer.commit(rm1);
          case ABORT -> inMemoryTransactionManagerServer.abort(rm1);
          default -> throw new RuntimeException("Unexpected action: {}" + action);
        }
      }).isInstanceOf(IllegalStateException.class);
    }
    assertThat(resourceManagerStatesRefCell.getData())
        .isEqualTo(ImmutableMap.of(
            RESOURCE_MANAGER_0, expectedRm0State,
            RESOURCE_MANAGER_1, expectedRm1State
        ));
  }

  @SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
      justification = "this is just a sample")
  private enum Action {
    ABORT,
    COMMIT,
  }
}