package overcooked.core;

import java.util.HashMap;
import java.util.Map;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;


/**
 * The object that is responsible for merging the local state into the global state.
 * This is needed as not all action affects all actors of the system and the global state transition
 * affects only part of the local states in it.
 * TODO: cloning the entire state is a must
 */
public class StateMerger {
  /**
   * Merges the local states into the global state.
   *
   * @param globalState the {@link GlobalState} to have the local states merged in
   * @param localStates the {@link LocalState}s to be merged into the {@link GlobalState}
   * @return a new {@link GlobalState}
   */
  public GlobalState merge(GlobalState globalState, Map<ActorDefinition, LocalState> localStates) {
    HashMap<ActorDefinition, LocalState> shallowCopy = new HashMap<>(globalState.getLocalStates());
    shallowCopy.putAll(localStates);
    return new GlobalState(shallowCopy);
  }
}
