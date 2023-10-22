package overcooked.core;

import overcooked.core.action.ActionTaker;
import overcooked.core.action.ActionTemplateMaterialiser;
import overcooked.core.action.IntransitiveActionTaker;
import overcooked.core.action.IntransitiveActionTemplateExecutor;
import overcooked.core.action.TransitiveActionTaker;
import overcooked.core.action.TransitiveActionTemplateExecutor;
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
    ActionTaker actionTaker = new ActionTaker();
    ActionTemplateMaterialiser materialiser = new ActionTemplateMaterialiser();
    return StateMachine.builder()
        .stateMachineDriver(StateMachineDriver.builder()
            .stateMerger(new StateMerger())
            .intransitiveActionTemplateExecutor(IntransitiveActionTemplateExecutor.builder()
                .intransitiveActionTaker(IntransitiveActionTaker.builder()
                    .materialiser(materialiser)
                    .actionTaker(actionTaker)
                    .build())
                .config(actorStateTransformerConfig)
                .build())
            .transitiveActionTemplateExecutor(TransitiveActionTemplateExecutor.builder()
                .transitiveActionTaker(TransitiveActionTaker.builder()
                    .materialiser(materialiser)
                    .actionTaker(actionTaker)
                    .build())
                .config(actorStateTransformerConfig)
                .build())
            .build())
        .globalStateVerifier(globalStateVerifier)
        .build();
  }
}
