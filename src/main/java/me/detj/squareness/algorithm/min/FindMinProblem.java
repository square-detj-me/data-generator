package me.detj.squareness.algorithm.min;

import lombok.Builder;
import lombok.Value;

import java.util.function.Function;

@Value
@Builder
public class FindMinProblem<O extends Comparable<O>, I> {
    private final I initialInput;
    private final Function<I, I> incrementFunction;
    private final int increments;
    private final Function<I, O> function;
}
