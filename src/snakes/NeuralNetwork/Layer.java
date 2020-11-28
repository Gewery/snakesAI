package snakes.NeuralNetwork;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import snakes.NeuralNetwork.ActivationFunctions.ActivationFunction;

public class Layer {

    List<List<Double>> incomingEdges;
    List<Double> biases;
    List<ActivationFunction> activationFunctions;

    public Layer() {
        incomingEdges = new LinkedList<>();
        activationFunctions = new LinkedList<>();
        biases = new LinkedList<>();
    }

    public List<Double> calculate(List<Double> inputData) {
        List<Double> resultData = new ArrayList<>();

        for (int currentNode = 0; currentNode < incomingEdges.size(); currentNode++) {
            double result = biases.get(currentNode);
            for (int prevNode = 0; prevNode < incomingEdges.get(currentNode).size(); prevNode++) {
                result += inputData.get(prevNode) * incomingEdges.get(currentNode).get(prevNode);
            }
            resultData.add(activationFunctions.get(currentNode).activate(result));
        }

        return resultData;
    }
}
