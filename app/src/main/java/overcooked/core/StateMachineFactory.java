package overcooked.core;

import overcooked.core.action.ActionExecutorFactory;
import overcooked.core.actor.ActorStateTransformerConfig;

/**
 * The factory class of {@link StateMachine}.
 */
public class StateMachineFactory {
  /**
   * Creates a {@link StateMachine} object.
   *
   * @param globalStateVerifier the {@link GlobalStateVerifier} object
   * @param actorStateTransformerConfig the config providing the transformer implementation between
   *                                    actor and {@link overcooked.core.actor.LocalState}.
   * @return a {@link StateMachine} object
   */
  public static StateMachine create(GlobalStateVerifier globalStateVerifier,
                                    ActorStateTransformerConfig actorStateTransformerConfig) {
    return StateMachine.builder()
        .stateMachineDriver(StateMachineDriver.builder()
            .stateMerger(new StateMerger())
            .intransitiveActionTemplateExecutor(
                ActionExecutorFactory.createIntransitiveActionTemplateExecutor(
                    actorStateTransformerConfig))
            .transitiveActionTemplateExecutor(
                ActionExecutorFactory.createTransitiveActionTemplateExecutor(
                    actorStateTransformerConfig))
            .build())
        .globalStateVerifier(globalStateVerifier)
        .build();
  }
}
