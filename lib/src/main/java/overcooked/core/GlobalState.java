package overcooked.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import overcooked.core.actor.ActorDefinition;
import overcooked.core.actor.LocalState;

/**
 * Represents a state of the global state machine.
 */
@EqualsAndHashCode(exclude = {"id"})
public class GlobalState {
  private static final AtomicLong SEED = new AtomicLong(0);
  @Getter
  private final long id;

  // <actor, localState>
  private final ImmutableMap<ActorDefinition, LocalState> localStates;

  public GlobalState(Map<ActorDefinition, LocalState> localStates) {
    this.localStates = ImmutableMap.copyOf(localStates);
    this.id = SEED.getAndIncrement();
  }

  public Set<ActorDefinition> getActorDefinitions() {
    return localStates.keySet();
  }

  public LocalState getCopyOfLocalState(ActorDefinition actorDefinition) {
    return Preconditions.checkNotNull(localStates.get(actorDefinition)).deepCopy();
  }

  public Map<ActorDefinition, LocalState> getCopyOfLocalStates() {
    return localStates.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().deepCopy()));
  }

}
