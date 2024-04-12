package overcooked.core.actor;

import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Value;

/**
 * The transformers between actors and their {@link ActorState}.
 */
@Value
@Builder
public class ActorStateTransformerConfig {
  ImmutableMap<ActorId, ActorFactory<?>> actorFactories;
  ImmutableMap<ActorId, ActorStateExtractor<?>> actorStateExtractors;
}
