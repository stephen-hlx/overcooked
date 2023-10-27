package overcooked.core;

import com.google.common.collect.ImmutableMap;
import java.util.Set;
import lombok.Value;
import overcooked.core.action.ActionTemplate;
import overcooked.core.actor.ActorDefinition;

/**
 * The configuration of the relationship between {@link ActorDefinition} and {@link ActionTemplate}.
 */
@Value
public class ActorActionConfig {
  ImmutableMap<ActorDefinition, Set<ActionTemplate>> actionDefinitionTemplates;
}