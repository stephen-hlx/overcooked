package overcooked.core;

import com.google.common.collect.ImmutableMap;
import lombok.Value;
import overcooked.core.action.ActionTemplate;
import overcooked.core.actor.ActorDefinition;

import java.util.Map;
import java.util.Set;

@Value
public class ActorActionConfig {
    ImmutableMap<ActorDefinition, Set<ActionTemplate>> actionDefinitionTemplates;
}
