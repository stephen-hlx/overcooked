package overcooked.core;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

/**
 * Represents a state of the global state machine.
 */
@Getter
@EqualsAndHashCode
public class GlobalState {
  // <actor, localState>
  private final ImmutableMap<ActorDefinition, LocalState> localStates;

  public GlobalState(Map<ActorDefinition, LocalState> localStates) {
    this.localStates = ImmutableMap.copyOf(localStates);
  }

  @Override
  public String toString() {
    return String.format("GlobalState(%s)", printLocalStates());
  }

  private String printLocalStates() {
    return localStates.entrySet().stream()
        .map(entry -> String.format("%s(%s)",
            entry.getKey().getId(),
            entry.getValue().toString()))
        .collect(Collectors.joining(","));
  }
}
