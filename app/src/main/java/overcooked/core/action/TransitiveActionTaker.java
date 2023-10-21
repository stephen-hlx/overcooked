package overcooked.core.action;

import lombok.Builder;

/**
 * The object that is responsible for taking the transitive action provided.
 */
@Builder
public class TransitiveActionTaker {
  private final ActionTemplateMaterialiser materialiser;
  private final ActionTaker actionTaker;

  /**
   * Takes the {@link TransitiveAction} provided.
   *
   * @param transitiveAction the transitive action
   */
  public void take(TransitiveAction transitiveAction) {
    ActionDefinition materialised = materialiser.materialise(transitiveAction.getActionTemplate(),
        transitiveAction.getActionReceiver().getClass(),
        transitiveAction.getActionReceiver());

    actionTaker.take(transitiveAction.getActionPerformer(),
        materialised);
  }

}
