package overcooked.core;

/**
 * A class that is responsible for validating the {@link GlobalState}.
 */
public interface InvariantVerifier {
  /**
   * Verify if the invariant holds in the provided {@link GlobalState}.
   *
   * @param globalState the {@link GlobalState} to be verified against the defined invariant
   * @return true if the invariant holds, false otherwise
   */
  boolean verify(GlobalState globalState);
}
