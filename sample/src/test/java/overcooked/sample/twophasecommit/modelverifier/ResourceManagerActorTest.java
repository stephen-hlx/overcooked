package overcooked.sample.twophasecommit.modelverifier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.PREPARED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import overcooked.sample.twophasecommit.model.ResourceManagerState;
import overcooked.sample.twophasecommit.model.ResourceManagerStateDao;

class ResourceManagerActorTest {
  private static final String RESOURCE_MANAGER_ID = "0";
  private final ResourceManagerStateDao stateDao = mock(ResourceManagerStateDao.class);

  static Object[][] passive_action_cases() {
    return new Object[][] {
        // current,   action,               success, expected
        {  WORKING,   PassiveAction.ABORT,  true,    ABORTED  },
        {  WORKING,   PassiveAction.COMMIT, false,   WORKING  },
        {  PREPARED,  PassiveAction.ABORT,  true,    ABORTED  },
        {  PREPARED,  PassiveAction.COMMIT, true,    COMMITTED},
        {  COMMITTED, PassiveAction.ABORT,  false,   COMMITTED},
        {  COMMITTED, PassiveAction.COMMIT, true,    COMMITTED},
        {  ABORTED,   PassiveAction.ABORT,  true,    ABORTED  },
        {  ABORTED,   PassiveAction.COMMIT, false,   ABORTED  },
    };
  }

  @ParameterizedTest
  @MethodSource("passive_action_cases")
  void passive_action_works(ResourceManagerState currentState,
                            PassiveAction action,
                            boolean success,
                            ResourceManagerState expectedState) {
    when(stateDao.get()).thenReturn(currentState);
    ResourceManagerActor resourceManagerActor =
            new ResourceManagerActor(RESOURCE_MANAGER_ID, stateDao);

    if (success) {
      doAction(resourceManagerActor, action);
      verify(stateDao).save(expectedState);
    } else {
      assertThatThrownBy(() -> doAction(resourceManagerActor, action))
          .isInstanceOf(IllegalStateException.class);
    }
    verify(stateDao).get();
    verifyNoMoreInteractions(stateDao);
  }

  static Object[][] proactive_action_cases() {
    return new Object[][] {
        // current,   action,                     success, expected
        {  WORKING,   ProactiveAction.SELF_ABORT, true,    ABORTED  },
        {  WORKING,   ProactiveAction.PREPARE,    true,    PREPARED },
        {  PREPARED,  ProactiveAction.SELF_ABORT, false,   PREPARED },
        {  PREPARED,  ProactiveAction.PREPARE,    true,    PREPARED },
        {  COMMITTED, ProactiveAction.SELF_ABORT, false,   COMMITTED},
        {  COMMITTED, ProactiveAction.PREPARE,    false,   COMMITTED},
        {  ABORTED,   ProactiveAction.SELF_ABORT, false,   ABORTED  },
        {  ABORTED,   ProactiveAction.PREPARE,    false,   ABORTED  },
    };
  }

  @ParameterizedTest
  @MethodSource("proactive_action_cases")
  void proactive_action_works(ResourceManagerState currentState,
                              ProactiveAction action,
                              boolean success,
                              ResourceManagerState expectedState) {
    when(stateDao.get()).thenReturn(currentState);
    ResourceManagerActor resourceManagerActor =
        new ResourceManagerActor(RESOURCE_MANAGER_ID, stateDao);

    TransactionManagerActor transactionManagerActor = mock(TransactionManagerActor.class);

    if (success) {
      doAction(resourceManagerActor, action, transactionManagerActor);
      verify(stateDao).save(expectedState);
    } else {
      assertThatThrownBy(() -> doAction(resourceManagerActor, action, transactionManagerActor))
          .isInstanceOf(IllegalStateException.class);
    }
    verify(stateDao).get();
    verifyNoMoreInteractions(stateDao);
  }

  private void doAction(ResourceManagerActor resourceManagerActor, PassiveAction action) {
    switch (action) {
      case COMMIT -> resourceManagerActor.commit();
      case ABORT -> resourceManagerActor.abort();
      default -> throw new RuntimeException("Unexpected new state: {}" + action);
    }
  }

  private void doAction(ResourceManagerActor resourceManagerActor,
                        ProactiveAction action,
                        TransactionManagerActor transactionManagerActor) {
    switch (action) {
      case PREPARE -> {
        resourceManagerActor.prepare(transactionManagerActor);
        verify(transactionManagerActor).prepare(RESOURCE_MANAGER_ID);
      }
      case SELF_ABORT -> {
        resourceManagerActor.selfAbort(transactionManagerActor);
        verify(transactionManagerActor).abort(RESOURCE_MANAGER_ID);
      }
      default -> throw new RuntimeException("Unexpected new state: {}" + action);
    }
  }

  @SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
      justification = "this is just a sample")
  private enum PassiveAction {
    ABORT,
    COMMIT,
  }

  @SuppressFBWarnings(value = "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
      justification = "this is just a sample")
  private enum ProactiveAction {
    SELF_ABORT,
    PREPARE,
  }
}