package overcooked.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import overcooked.core.actor.Actor;
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
  private final ImmutableMap<Actor, LocalState> localStates;

  public GlobalState(Map<Actor, LocalState> localStates) {
    this.localStates = ImmutableMap.copyOf(localStates);
    this.id = SEED.getAndIncrement();
  }

  public Set<Actor> getActorDefinitions() {
    return localStates.keySet();
  }

  public LocalState getCopyOfLocalState(Actor actor) {
    return Preconditions.checkNotNull(localStates.get(actor)).deepCopy();
  }

  public Map<Actor, LocalState> getCopyOfLocalStates() {
    return localStates.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().deepCopy()));
  }

}
