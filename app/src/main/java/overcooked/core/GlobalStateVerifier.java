package overcooked.core;

/**
 * A class that is responsible for validating the {@link GlobalState}.
 */
public interface GlobalStateVerifier {
  boolean validate(GlobalState globalState);
}
