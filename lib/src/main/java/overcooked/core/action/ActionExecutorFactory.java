package overcooked.core.action;

import overcooked.core.actor.ActorState;
import overcooked.core.actor.ActorStateTransformerConfig;

/**
 * The factory of both {@link IntransitiveActionTemplateExecutor} and
 * {@link TransitiveActionTemplateExecutor}.
 */
public class ActionExecutorFactory {
  private static final ActionTaker ACTION_TAKER = new ActionTaker();

  /**
   * Creates an {@link IntransitiveActionTemplateExecutor} object.
   *
   * @param actorStateTransformerConfig the config providing the transformer implementation between
   *                                    actor and {@link ActorState}.
   * @return the {@link IntransitiveActionTemplateExecutor} object created
   */
  public static IntransitiveActionTemplateExecutor createIntransitiveActionTemplateExecutor(
      ActorStateTransformerConfig actorStateTransformerConfig) {
    return IntransitiveActionTemplateExecutor.builder()
        .failureRecordingOverrider(new FailureRecordingOverrider())
        .actionTaker(ACTION_TAKER)
        .config(actorStateTransformerConfig)
        .build();
  }

  /**
   * Creates an {@link TransitiveActionTemplateExecutor} object.
   *
   * @param actorStateTransformerConfig the config providing the transformer implementation between
   *                                    actor and {@link ActorState}.
   * @return the {@link TransitiveActionTemplateExecutor} object created
   */
  public static TransitiveActionTemplateExecutor createTransitiveActionTemplateExecutor(
      ActorStateTransformerConfig actorStateTransformerConfig) {
    return TransitiveActionTemplateExecutor.builder()
        .failureInjector(new FailureInjector())
        .actionTaker(ACTION_TAKER)
        .config(actorStateTransformerConfig)
        .build();
  }
}
