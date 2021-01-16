package snakes.NeuralNetwork.ActivationFunctions;

import java.lang.Math;

public class Sigmoid implements ActivationFunction {
    @Override
    public double activate(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    @Override
    public ActivationFunction copy() {
        return new Sigmoid();
    }
}
