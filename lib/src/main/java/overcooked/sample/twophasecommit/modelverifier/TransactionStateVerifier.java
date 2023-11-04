package overcooked.sample.twophasecommit.modelverifier;

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
    return ((TransactionManagerLocalState) (globalState.getLocalStates().entrySet().stream()
        .filter(entry -> entry.getKey().getId().equals(transactionManagerId))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Could not find TransactionManagerLocalState"))
        .getValue())).getResourceManagerStates().values();
  }

  private Collection<ResourceManagerState> getResourceManagerStatesFromResourceManagers(
      GlobalState globalState) {
    return globalState.getLocalStates().entrySet().stream()
        .filter(entry -> !entry.getKey().getId().equals(transactionManagerId))
        .map(Map.Entry::getValue)
        .map(o -> ((ResourceManagerLocalState) o).getState())
        .collect(Collectors.toSet());
  }

  private static boolean verifyResourceManagerState(
      Collection<ResourceManagerState> resourceManagerStates) {
    if (resourceManagerStates.contains(ResourceManagerState.ABORTED)) {
      return !resourceManagerStates.contains(ResourceManagerState.COMMITTED);
    } else if (resourceManagerStates.contains(ResourceManagerState.COMMITTED)) {
      return !resourceManagerStates.contains(ResourceManagerState.ABORTED);
    }
    return true;
  }
}
