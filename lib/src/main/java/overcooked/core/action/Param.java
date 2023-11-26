package overcooked.core.action;

/**
 * A parameter that can be either a template or a value.
 * A value is one that is ready to use. But a template needs to be materialised.
 */
interface Param {
  boolean isTemplate();

  Class<?> getType();
}
