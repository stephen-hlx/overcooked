package overcooked.sample.twophasecommit.modelverifier;

import static overcooked.sample.twophasecommit.model.ResourceManagerState.ABORTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.COMMITTED;
import static overcooked.sample.twophasecommit.model.ResourceManagerState.WORKING;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import overcooked.core.GlobalState;
import overcooked.core.GlobalStateVerifier;
import overcooked.sample.twophasecommit.model.ResourceManagerState;

@RequiredArgsConstructor
class TransactionStateVerifier implements GlobalStateVerifier {
  private final String transactionManagerId;

  @Override
  public boolean validate(GlobalState globalState) {
    return verifyResourceManagerState(getResourceManagerStatesFromTransactionManager(globalState))
        && verifyResourceManagerState(getResourceManagerStatesFromResourceManagers(globalState));
  }

  private Collection<ResourceManagerState> getResourceManagerStatesFromTransactionManager(
      GlobalState globalState) {
    return ((TransactionManagerLocalState) (globalState.getCopyOfLocalStates().entrySet().stream()
        .filter(entry -> entry.getKey().getId().equals(transactionManagerId))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Could not find TransactionManagerLocalState"))
        .getValue())).getResourceManagerStates().values();
  }

  private Collection<ResourceManagerState> getResourceManagerStatesFromResourceManagers(
      GlobalState globalState) {
    return globalState.getCopyOfLocalStates().entrySet().stream()
        .filter(entry -> !entry.getKey().getId().equals(transactionManagerId))
        .map(Map.Entry::getValue)
        .map(o -> ((ResourceManagerLocalState) o).getState())
        .collect(Collectors.toSet());
  }

  private static boolean verifyResourceManagerState(
      Collection<ResourceManagerState> resourceManagerStates) {
    if (resourceManagerStates.contains(ABORTED)) {
      return !resourceManagerStates.contains(COMMITTED);
    } else if (resourceManagerStates.contains(COMMITTED)) {
      return !(resourceManagerStates.contains(ABORTED)
          || resourceManagerStates.contains(WORKING));
    }
    return true;
  }
}
