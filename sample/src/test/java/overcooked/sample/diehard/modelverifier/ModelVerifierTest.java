package overcooked.sample.diehard.modelverifier;

import org.junit.jupiter.api.Test;

class ModelVerifierTest {
  @Test
  void can_run_without_error() {
    new ModelVerifier().verify();
  }

}