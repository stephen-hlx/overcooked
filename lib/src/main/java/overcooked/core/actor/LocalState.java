package overcooked.core.actor;

import com.google.gson.Gson;

/**
 * The state of an actor in a system that consists of multiple actors.
 */
public abstract class LocalState {
  private static final Gson GSON = new Gson();

  public LocalState deepCopy() {
    return GSON.fromJson(GSON.toJson(this), this.getClass());
  }
}
