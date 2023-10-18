package overcooked.core.action;

import com.google.common.collect.ImmutableList;

public class ActionTemplateMaterialiser {
    // TODO: it is not that obvious the materialisation is for only one param template
    public  ActionDefinition materialise(
        ActionTemplate template,
        Class<?> clazz,
        Object value) {
        return ActionDefinition.builder()
            .actionType(template.getActionType())
            .methodName(template.getMethodName())
            .parameters(template.getParameters().stream()
                .map(param ->
                    param.isTemplate()
                    ? new ParamValue(clazz, value)
                    : (ParamValue) param)
                .collect(ImmutableList.toImmutableList()))
            .build();
    }

    // TODO: It is not obvious that no value replacement happens here
    public ActionDefinition materialise(ActionTemplate actionTemplate) {
        return materialise(actionTemplate, null, null);
    }
}
