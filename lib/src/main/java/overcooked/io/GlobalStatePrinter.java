package overcooked.io;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import overcooked.core.GlobalState;
import overcooked.core.actor.ActorId;
import overcooked.core.actor.LocalState;

/**
 * This class can be made package private.
 */
class GlobalStatePrinter {
  private static final Comparator<Map.Entry<ActorId, LocalState>> ACTOR_COMPARATOR =
      Comparator.comparing(o -> o.getKey().getId());

  static String print(GlobalState globalState) {
    return globalState.getCopyOfLocalStates().entrySet().stream()
        .sorted(ACTOR_COMPARATOR)
        .map(GlobalStatePrinter::printEntry)
        .collect(Collectors.joining(", "));
  }

  private static String printEntry(Map.Entry<ActorId, LocalState> entry) {
    return String.format("%s(%s)", entry.getKey().getId(), entry.getValue());
  }
}
