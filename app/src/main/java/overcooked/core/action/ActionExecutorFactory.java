package overcooked.core.action;

import overcooked.core.actor.ActorStateTransformerConfig;

/**
 * The factory of both {@link IntransitiveActionTemplateExecutor} and
 * {@link TransitiveActionTemplateExecutor}.
 */
public class ActionExecutorFactory {
  private static final ActionTemplateMaterialiser MATERIALISER = new ActionTemplateMaterialiser();
  private static final ActionTaker ACTION_TAKER = new ActionTaker();

  /**
   * Creates an {@link IntransitiveActionTemplateExecutor} object.
   *
   * @param actorStateTransformerConfig the config providing the transformer implementation between
   *                                    actor and {@link overcooked.core.actor.LocalState}.
   * @return the {@link IntransitiveActionTemplateExecutor} object created
   */
  public static IntransitiveActionTemplateExecutor createIntransitiveActionTemplateExecutor(
      ActorStateTransformerConfig actorStateTransformerConfig) {
    return IntransitiveActionTemplateExecutor.builder()
        .intransitiveActionTaker(IntransitiveActionTaker.builder()
            .actionTaker(ACTION_TAKER)
            .materialiser(MATERIALISER)
            .build())
        .config(actorStateTransformerConfig)
        .build();
  }

  /**
   * Creates an {@link TransitiveActionTemplateExecutor} object.
   *
   * @param actorStateTransformerConfig the config providing the transformer implementation between
   *                                    actor and {@link overcooked.core.actor.LocalState}.
   * @return the {@link TransitiveActionTemplateExecutor} object created
   */
  public static TransitiveActionTemplateExecutor createTransitiveActionTemplateExecutor(
      ActorStateTransformerConfig actorStateTransformerConfig) {
    return TransitiveActionTemplateExecutor.builder()
        .transitiveActionTaker(TransitiveActionTaker.builder()
            .actionTaker(ACTION_TAKER)
            .materialiser(MATERIALISER)
            .build())
        .config(actorStateTransformerConfig)
        .build();
  }
}
