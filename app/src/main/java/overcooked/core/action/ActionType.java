package overcooked.core.action;

import overcooked.core.actor.ActorDefinition;

/**
 * Describes an action type, which can be either transitive or intransitive.
 */
public interface ActionType {
  boolean isTransitive();

  // TODO: review this default method, should it be here?
  default ActorDefinition getActionReceiverDefinition() {
    throw new UnsupportedOperationException();
  }
}
