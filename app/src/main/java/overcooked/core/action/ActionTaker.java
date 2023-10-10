package overcooked.core.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ActionTaker {
    public void take(Object actor,
                     ActionDefinition actionDefinition) {
        Method method = getMethod(actor.getClass(), actionDefinition);
        List<ParamValue> params = actionDefinition.getParameters();
        invoke(actor, method, params);
    }

    private static <ActorType> void invoke(ActorType actor,
                                           Method method,
                                           List<ParamValue> params) {
        Object[] parameters = params.stream()
            .map(ParamValue::getValue)
            .toArray(Object[]::new);
        try {
            method.invoke(actor, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private <ActorType> Method getMethod(Class<ActorType> actorClass, ActionDefinition actionDefinition) {
        Class<?>[] parameterTypes = actionDefinition.getParameters().stream()
            .map(ParamValue::getClazz)
            .toArray(Class<?>[]::new);
        try {
            return actorClass.getMethod(actionDefinition.getMethodName(), parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
