package overcooked.core.actor;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class ActorStateTransformerConfig {
    Map<ActorDefinition, ActorFactory<?>> actorFactories;
    Map<ActorDefinition, LocalStateExtractor> localStateExtractors;
}
