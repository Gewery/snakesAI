package snakes.GeneticProgramming.Operations;

public interface Operation {
    /**
     * Perform a binary operation on a and b
     * @param a first term
     * @param b second term
     * @return result of operation on a and b
     */
    double calculate(double a, double b);
}
