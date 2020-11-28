package snakes.NeuralNetwork.ActivationFunctions;

import static java.lang.Double.max;

public class ReLU implements ActivationFunction {
    @Override
    public double activate(double input) {
        return max(0, input);
    }
}
