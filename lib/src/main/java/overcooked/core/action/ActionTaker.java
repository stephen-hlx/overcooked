package overcooked.core.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * The object that is responsible for taking an action for an actor.
 */
class ActionTaker {
  /**
   * Takes the action defined in the {@link ActionDefinition} object on behalf of the actor.
   *
   * @param actor            the actor
   * @param actionDefinition the action to be performed
   */
  public void take(Object actor,
                   ActionDefinition actionDefinition) {
    Method method = getMethod(actor.getClass(), actionDefinition);
    List<ParamValue> params = actionDefinition.getParameters();
    invoke(actor, method, params);
  }

  private static <ActorT> void invoke(ActorT actor,
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

  private <ActorT> Method getMethod(Class<ActorT> actorClass,
                                    ActionDefinition actionDefinition) {
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
