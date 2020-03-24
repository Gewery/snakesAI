package snakes.GeneticProgramming.Operations;

public class Pow implements Operation {
    @Override
    public double calculate(double a, double b) {
        return (float)(Math.pow(a, b));
    }
}
