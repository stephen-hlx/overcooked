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
   * @param value          the value to be filled into the template
   * @return an {@link ActionDefinition} object that is created based on the parameter data
   *     provided
   */
  public ActionDefinition materialise(ActionTemplate actionTemplate, Object value) {
    Param param = actionTemplate.getParameter();
    return ActionDefinition.builder()
        .actionType(actionTemplate.getActionType())
        .methodName(actionTemplate.getMethodName())
        .paramValue(actionTemplate.getActionType().isTransitive()
            ? new ParamValue(param.getType(), value)
            : (ParamValue) param)
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
  public ActionDefinition materialise(ActionTemplate actionTemplate) {
    return materialise(actionTemplate, null);
  }
}
