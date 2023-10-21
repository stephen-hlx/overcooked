package overcooked.core.actor;

import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Value;

/**
 * The transformers between actors and their {@link LocalState}.
 */
@Value
@Builder
public class ActorStateTransformerConfig {
  ImmutableMap<ActorDefinition, ActorFactory<?>> actorFactories;
  ImmutableMap<ActorDefinition, LocalStateExtractor> localStateExtractors;
}
