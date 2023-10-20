package overcooked.core.visual;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import overcooked.core.analysis.Transition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DotGraphBuilderTest {
    private final TransitionPrinter transitionPrinter = mock(TransitionPrinter.class);
    private final DotGraphBuilder dotGraphBuilder = new DotGraphBuilder(transitionPrinter);

    @Test
    void prints_dot_format_string() {
        Transition transition1 = Transition.builder().methodName("transition1").build();
        when(transitionPrinter.print(transition1)).thenReturn("printedTransition1");

        Transition transition2 = Transition.builder().methodName("transition2").build();
        when(transitionPrinter.print(transition2)).thenReturn("printedTransition2");

        assertThat(dotGraphBuilder.build(ImmutableSet.of(
            transition1,
            transition2)))
            .isEqualTo("digraph G {\n\tprintedTransition1;\n\tprintedTransition2;\n}");

        verify(transitionPrinter).print(transition1);
        verify(transitionPrinter).print(transition2);
        verifyNoMoreInteractions(transitionPrinter);
    }

}