package snakes.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

    List<Layer> layers;

    public List<Double> calculate(List<Double> input) {
        for (Layer layer : layers.subList(1, layers.size())) {
            input = layer.calculate(input);
        }

        return input;
    }

    public NeuralNetwork() {
        layers = new ArrayList<>();
    }

    public NeuralNetwork copy() {
        NeuralNetwork copy = new NeuralNetwork();
        layers.forEach(layer -> copy.layers.add(layer.copy()));

        return copy;
    }
}
