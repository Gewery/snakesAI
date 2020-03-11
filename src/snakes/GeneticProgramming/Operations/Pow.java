package snakes.GeneticProgramming.Operations;

public class Pow implements Operation {
    @Override
    public float calculate(float a, float b) {
        return (float)(Math.pow(a, b));
    }
}
