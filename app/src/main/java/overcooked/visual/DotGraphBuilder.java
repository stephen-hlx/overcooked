package overcooked.visual;

import lombok.RequiredArgsConstructor;
import overcooked.analysis.Transition;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DotGraphBuilder {
    private final TransitionPrinter transitionPrinter;
    public String build(Set<Transition> transitions) {
        String content = transitions.stream()
            .map(transition -> buildLine(transitionPrinter.print(transition)))
            .collect(Collectors.joining("\n"));
        return String.format("digraph G {%n%s%n}", content);
    }

    private static String buildLine(String s) {
        return "\t" + s + ";";
    }
}
