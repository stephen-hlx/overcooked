package overcooked.core.action;

/**
 * An {@link ActionTemplate} is the blueprint of an action, it is not executable. It needs to be
 * materialised into an {@link ActionDefinition} before it can be performed.
 * TODO: it is not that obvious the materialisation is for only one param template
 */
class ActionTemplateMaterialiser {

  /**
   * Materialises the {@link ActionTemplate} provided.
   *
   * @param actionTemplate the action template
   * @param actionReceiver          the actionReceiver to be filled into the template
   * @return an {@link ActionDefinition} object that is created based on the parameter data
   *     provided
   */
  public <PerformerT, ReceiverT> ActionDefinition<PerformerT, ReceiverT> materialise(
      ActionTemplate<PerformerT, ReceiverT> actionTemplate, ReceiverT actionReceiver) {
    return ActionDefinition.<PerformerT, ReceiverT>builder()
        .actionPerformerDefinition(actionTemplate.getActionPerformerDefinition())
        .actionType(actionTemplate.getActionType())
        .actionLabel(actionTemplate.getActionLabel())
        .actionReceiver(actionReceiver)
        .action(actionTemplate.getAction())
        .build();
  }


  /**
   * Materialises the {@link ActionTemplate} provided.
   * TODO: It is not obvious that no value replacement happens here
   *
   * @param actionTemplate the action template
   * @return an {@link ActionDefinition} object that is created based on the parameter data
   *     provided
   */
  public <PerformerT, ReceiverT> ActionDefinition<PerformerT, ReceiverT> materialise(
      ActionTemplate<PerformerT, ReceiverT> actionTemplate) {
    return materialise(actionTemplate, null);
  }
}
