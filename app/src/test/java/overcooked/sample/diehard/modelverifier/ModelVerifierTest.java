package overcooked.sample.diehard.modelverifier;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import overcooked.core.GlobalStateVerificationException;

class ModelVerifierTest {
    @Test
    void works() {
        Assertions.assertThatThrownBy(() -> ModelVerifier.main(new String[]{""}))
            .isInstanceOf(GlobalStateVerificationException.class);
    }

}