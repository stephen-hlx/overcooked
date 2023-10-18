package overcooked.core;

public interface GlobalStateVerifier {
    void verify(GlobalState globalState) throws GlobalStateVerificationException;
}
