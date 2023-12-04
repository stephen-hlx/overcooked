package overcooked.core.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * The object that is responsible for taking an action for an actor.
 */
@Slf4j
class ActionTaker {
  /**
   * Takes the action defined in the {@link ActionDefinition} object on behalf of the actor.
   *
   * @param actor            the actor
   * @param actionDefinition the action to be performed
   */
  public ActionResult take(Object actor,
                   ActionDefinition actionDefinition) {
    Method method = getMethod(actor.getClass(), actionDefinition);
    List<ParamValue> params = actionDefinition.getParameters();
    return invoke(actor, method, params);
  }

  private static <ActorT> ActionResult invoke(ActorT actor,
                                      Method method,
                                      List<ParamValue> params) {
    Object[] parameters = params.stream()
        .map(ParamValue::getValue)
        .toArray(Object[]::new);
    try {
      method.invoke(actor, parameters);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      // TODO: no testing
      log.info(String.format("When %s.%s is called against %s Exception %s was thrown for cause %s",
          actor.getClass().getSimpleName(), method.getName(), params,
          e, e.getCause()));
      return ActionResult.failure(e.getCause());
    }

    return ActionResult.success();
  }

  private <ActorT> Method getMethod(Class<ActorT> actorClass,
                                    ActionDefinition actionDefinition) {
    Class<?>[] parameterTypes = actionDefinition.getParameters().stream()
        .map(ParamValue::getType)
        .toArray(Class<?>[]::new);
    try {
      return actorClass.getMethod(actionDefinition.getMethodName(), parameterTypes);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}
