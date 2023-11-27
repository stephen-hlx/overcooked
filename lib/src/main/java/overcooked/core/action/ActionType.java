package overcooked.core.action;

import overcooked.core.actor.Actor;

/**
 * Describes an action type, which can be either transitive or intransitive.
 */
public interface ActionType {
  boolean isTransitive();

  // TODO: review this default method, should it be here?
  default Actor getActionReceiverDefinition() {
    throw new UnsupportedOperationException();
  }
}
