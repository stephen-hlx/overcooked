package overcooked.sample.twophasecommit.modelverifier;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ModelVerifierTest {
  @Test
//  @Disabled
  void can_run_without_error() {
    new ModelVerifier().verify();
  }

}