package overcooked.core;

import com.google.common.collect.ImmutableMap;
import java.util.Set;
import lombok.Value;
import overcooked.core.action.ActionTemplate;
import overcooked.core.actor.Actor;

/**
 * The configuration of the relationship between {@link Actor} and {@link ActionTemplate}.
 */
@Value
public class ActorActionConfig {
  ImmutableMap<Actor, Set<ActionTemplate>> actionDefinitionTemplates;
}
