package snakes.NeuralNetwork;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    public Layer copy() {
        Layer copy = new Layer();
        copy.biases = new LinkedList<>(this.biases);
        this.activationFunctions.forEach(activationFunction -> copy.activationFunctions.add(activationFunction.copy()));
        this.incomingEdges.forEach(innerIncomingEdges -> copy.incomingEdges.add(new LinkedList<>(innerIncomingEdges)));

        return copy;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(biases.size() + "\n");

        for (Double bias : biases) {
            result.append(bias).append(" ");
        }
        result.append("\n");

        for (ActivationFunction activationFunction : activationFunctions) {
            result.append(activationFunction.getClass().getSimpleName()).append(" ");
        }
        result.append("\n");

        for (List<Double> incomingEdge : incomingEdges) {
            for (Double edgeValue : incomingEdge) {
                result.append(edgeValue).append(" ");
            }
            result.append("\n");
        }

        return result.toString();
    }
}
