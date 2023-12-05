package overcooked.core.action;

import lombok.Builder;

/**
 * The object that is responsible for taking the intransitive action.
 */
@Builder
class IntransitiveActionTaker {
  private final ActionTemplateMaterialiser materialiser;
  private final ActionTaker actionTaker;

  /**
   * Takes the {@link IntransitiveAction} provided.
   *
   * @param intransitiveAction the intransitive action
   * @return the {@link ActionResult} object
   */
  public ActionResult take(IntransitiveAction intransitiveAction) {
    ActionDefinition materialised =
        materialiser.materialise(intransitiveAction.getActionTemplate());

    return actionTaker.take(intransitiveAction.getActor(), materialised);
  }

}
