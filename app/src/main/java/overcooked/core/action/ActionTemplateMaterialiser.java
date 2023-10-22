package overcooked.core.action;

import com.google.common.collect.ImmutableList;

/**
 * An {@link ActionTemplate} is the blueprint of an action, it is not executable. It needs to be
 * materialised into an {@link ActionDefinition} before it can be performed.
 * TODO: it is not that obvious the materialisation is for only one param template
 */
public class ActionTemplateMaterialiser {

  /**
   * Materialises the {@link ActionTemplate} provided.
   *
   * @param actionTemplate the action template
   * @param clazz          the type of the value to be filled into the template
   * @param value          the value to be filled into the template
   * @return an {@link ActionDefinition} object that is created based on the parameter data
   *     provided
   */
  public ActionDefinition materialise(
      ActionTemplate actionTemplate,
      Class<?> clazz,
      Object value) {
    return ActionDefinition.builder()
        .actionType(actionTemplate.getActionType())
        .methodName(actionTemplate.getMethodName())
        .parameters(actionTemplate.getParameters().stream()
            .map(param ->
                param.isTemplate()
                    ? new ParamValue(clazz, value)
                    : (ParamValue) param)
            .collect(ImmutableList.toImmutableList()))
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
    return materialise(actionTemplate, null, null);
  }
}
