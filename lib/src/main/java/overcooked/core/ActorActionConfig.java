package overcooked.core;

import com.google.common.collect.ImmutableMap;
import java.util.Set;
import lombok.Value;
import overcooked.core.action.ActionTemplate;
import overcooked.core.actor.ActorId;

/**
 * The configuration of the relationship between {@link ActorId} and {@link ActionTemplate}.
 */
@Value
public class ActorActionConfig {
  ImmutableMap<ActorId, Set<ActionTemplate<?, ?>>> actionTemplates;
}
