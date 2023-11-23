package overcooked.core;

import java.util.Map;
import java.util.stream.Collectors;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;


/**
 * The object that is responsible for merging the local state into the global state.
 * This is needed as not all action affects all actors of the system and the global state transition
 * affects only part of the local states in it.
 */
class StateMerger {
  /**
   * Merges the local states into the global state.
   *
   * @param globalState the {@link GlobalState} to have the local states merged in
   * @param localStates the {@link LocalState}s to be merged into the {@link GlobalState}
   * @return a new {@link GlobalState}
   */
  GlobalState merge(GlobalState globalState, Map<ActorDefinition, LocalState> localStates) {
    Map<ActorDefinition, LocalState> deepCopy =
        globalState.getLocalStates().entrySet().stream()
            .filter(entry -> !localStates.containsKey(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().deepCopy()));
    deepCopy.putAll(localStates);
    return new GlobalState(deepCopy);
  }
}
