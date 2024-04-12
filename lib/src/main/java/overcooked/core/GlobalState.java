package overcooked.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.ActorState;

/**
 * Represents a state of the global state machine.
 */
@EqualsAndHashCode(exclude = {"id"})
public class GlobalState {
  private static final AtomicLong SEED = new AtomicLong(0);
  @Getter
  private final long id;

  // <actor, localState>
  private final ImmutableMap<ActorId, ActorState> localStates;

  public GlobalState(Map<ActorId, ActorState> localStates) {
    this.localStates = ImmutableMap.copyOf(localStates);
    this.id = SEED.getAndIncrement();
  }

  public Set<ActorId> getActorIds() {
    return localStates.keySet();
  }

  public ActorState getCopyOfLocalState(ActorId actorId) {
    return Preconditions.checkNotNull(localStates.get(actorId)).deepCopy();
  }

  public Map<ActorId, ActorState> getCopyOfLocalStates() {
    return localStates.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().deepCopy()));
  }

}
