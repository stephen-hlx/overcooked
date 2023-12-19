package overcooked.visual;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import overcooked.analysis.Arc;
import overcooked.core.actor.ActorId;

class ArcPrinterTest {
  @Test
  void works_with_action_receiver() {
    assertThat(ArcPrinter.printArc(Arc.builder()
            .actionPerformerId(ActorId.builder().id("performer").build())
            .label("method")
            .actionReceiverId(ActorId.builder().id("receiver").build())
        .build()))
        .isEqualTo("performer.method(receiver)");
  }

  @Test
  void works_without_action_receiver() {
    assertThat(ArcPrinter.printArc(Arc.builder()
        .actionPerformerId(ActorId.builder().id("performer").build())
        .label("method")
        .actionReceiverId(null)
        .build()))
        .isEqualTo("performer.method()");
  }
}