package snakes.NeuralNetwork.ActivationFunctions;

public interface ActivationFunction {

    double activate(double input);

    ActivationFunction copy();
}
