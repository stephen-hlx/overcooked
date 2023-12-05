package overcooked.core.action;

import lombok.Builder;

/**
 * The object that is responsible for taking the transitive action provided.
 * TODO: this is looking increasingly like its brother. Can we combine them?
 */
@Builder
class TransitiveActionTaker {
  private final ActionTemplateMaterialiser materialiser;
  private final ActionTaker actionTaker;

  /**
   * Takes the {@link TransitiveAction} provided.
   *
   * @param transitiveAction the transitive action
   * @return the {@link ActionResult} object
   */
  public ActionResult take(TransitiveAction transitiveAction) {
    ActionDefinition materialised = materialiser.materialise(transitiveAction.getActionTemplate(),
        transitiveAction.getActionReceiver());

    return actionTaker.take(transitiveAction.getActionPerformer(), materialised);
  }
}
