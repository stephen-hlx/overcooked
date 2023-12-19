package overcooked.core.action;

import overcooked.core.actor.ActorId;

/**
 * Describes an action type, which can be either transitive or intransitive.
 */
public interface ActionType {
  boolean isTransitive();

  // TODO: review this default method, should it be here?
  default ActorId getActionReceiverId() {
    throw new UnsupportedOperationException();
  }
}
