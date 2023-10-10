package overcooked.core;

import lombok.Value;
import overcooked.core.action.ActionTemplate;
import overcooked.core.actor.ActorDefinition;

import java.util.Map;
import java.util.Set;

@Value
public class ActorActionConfig {
    Map<ActorDefinition, Set<ActionTemplate>> actionDefinitionTemplates;
}
