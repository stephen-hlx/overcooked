package overcooked.sample.twophasecommit.modelverifier;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import overcooked.core.GlobalState;
import overcooked.core.InvariantVerifier;
import overcooked.core.actor.ActorId;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

@RequiredArgsConstructor
class TransactionStateVerifier implements InvariantVerifier {
  private final ActorId transactionManagerId;

  @Override
  public boolean verify(GlobalState globalState) {
    Map<String, ResourceManagerState> transactionManagerView =
        getResourceManagerStatesFromTransactionManager(globalState);
    Map<String, ResourceManagerState> resourceManagerStates =
        getResourceManagerStatesFromResourceManagers(globalState);
    return verifyResourceManagerState(transactionManagerView)
        && verifyResourceManagerState(resourceManagerStates)
        && transactionManagerView.equals(resourceManagerStates);
  }

  private Map<String, ResourceManagerState> getResourceManagerStatesFromTransactionManager(
      GlobalState globalState) {
    return ((TransactionManagerActorState) (globalState.getCopyOfLocalStates().entrySet().stream()
        .filter(entry -> entry.getKey().equals(transactionManagerId))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Could not find TransactionManagerActorState"))
        .getValue())).getResourceManagerStates();
  }

  private Map<String, ResourceManagerState> getResourceManagerStatesFromResourceManagers(
      GlobalState globalState) {
    return globalState.getCopyOfLocalStates().entrySet().stream()
        .filter(entry -> !entry.getKey().equals(transactionManagerId))
        .collect(Collectors.toMap(
            e -> e.getKey().getId(),
            e -> ((ResourceManagerActorState) e.getValue()).getState()));
  }

  private static boolean verifyResourceManagerState(
      Map<String, ResourceManagerState> resourceManagerStates) {
    if (resourceManagerStates.containsValue(ABORTED)) {
      return !resourceManagerStates.containsValue(COMMITTED);
    } else if (resourceManagerStates.containsValue(COMMITTED)) {
      return !(resourceManagerStates.containsValue(ABORTED)
          || resourceManagerStates.containsValue(WORKING));
    }
    return true;
  }
}
