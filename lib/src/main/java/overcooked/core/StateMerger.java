package overcooked.core;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import overcooked.core.actor.Actor;
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
  GlobalState merge(GlobalState globalState, Map<Actor, LocalState> localStates) {
    Map<Actor, LocalState> deepCopy =
        globalState.getActorDefinitions().stream()
            // The LocalStates are going to overwrite whatever the GlobalState has at the moment.
            // Filtering them out is an optimisation to reduce the number of objects getting created
            // in the heap.
            .filter(actorDefinition -> !localStates.containsKey(actorDefinition))
            .collect(Collectors.toMap(
                Function.identity(),
                actorDefinition -> globalState.getCopyOfLocalState(actorDefinition).deepCopy()));
    deepCopy.putAll(localStates);
    return new GlobalState(deepCopy);
  }
}
