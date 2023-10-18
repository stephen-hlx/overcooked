package overcooked.core.actor;

import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class ActorStateTransformerConfig {
    ImmutableMap<ActorDefinition, ActorFactory<?>> actorFactories;
    ImmutableMap<ActorDefinition, LocalStateExtractor> localStateExtractors;
}
