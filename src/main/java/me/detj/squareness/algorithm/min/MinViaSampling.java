package me.detj.squareness.algorithm.min;

public class MinViaSampling {

    public static <I, O extends Comparable<O>> MinResult<I, O> minForFunction(FindMinProblem<O, I> problem) {
        I input = problem.getInitialInput();

        O minOutput = null;
        I inputForMinOutput = null;

        for(int i = 0; i < problem.getIncrements(); i++) {
            O result = problem.getFunction().apply(input);

            if (minOutput == null || result.compareTo(minOutput) < 0) {
                minOutput = result;
                inputForMinOutput = input;
            }

            input = problem.getIncrementFunction().apply(input);
        }

        return new MinResult(inputForMinOutput, minOutput);
    }
}
