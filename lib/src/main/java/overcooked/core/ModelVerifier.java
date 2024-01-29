package overcooked.core;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import overcooked.core.actor.ActorStateTransformerConfig;

/**
 * The model verifier.
 * It consists of all required information of a system to run formal verification against it.
 */
@Builder
@Slf4j
public class ModelVerifier {
  private final ActorActionConfig actorActionConfig;
  private final ActorStateTransformerConfig actorStateTransformerConfig;
  private final InvariantVerifier invariantVerifier;

  /**
   * Runs the model verification using the initial {@link GlobalState} provided.
   *
   * @param initialGlobalState the initial {@link GlobalState} of the system
   * @return a {@link StateMachineExecutionContext} object that contains the details of the
   *         execution
   */
  public StateMachineExecutionContext runWith(GlobalState initialGlobalState) {
    StateMachineExecutionContext stateMachineExecutionContext =
        new StateMachineExecutionContext(initialGlobalState);
    StateMachine stateMachine = StateMachineFactory
        .create(invariantVerifier, actorStateTransformerConfig);
    stateMachine.run(initialGlobalState, actorActionConfig, stateMachineExecutionContext);

    return stateMachineExecutionContext;
  }
}
