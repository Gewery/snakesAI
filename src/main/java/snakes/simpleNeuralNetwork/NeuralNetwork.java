package snakes.simpleNeuralNetwork;

import java.util.Arrays;
import java.util.LinkedList;

import org.ejml.simple.SimpleMatrix;

public class NeuralNetwork {

    private static final Double BIAS = 0.0;
    private static final double[] BIASES = new double[Population.FIRST_LAYER_NEURONS_NUMBER];

    LinkedList<double[][]> edgesWeights;
    //  [.123, .32, .23] [.23,  .34, .25] [.22,  .34, .25] [.23,  .34, .25] [.53,  .34, .25]
    //< [.23,  .34, .25],[.62,  .133,.05],[.12,  .103,.15],[.14,  .133,.05],[.12,  .133,.05] >
    //  [.12,  .133,.05] [.23,  .31, .25] [.23,  .34, .25] [.23,  .24, .25] [.23,  .36, .25]

    public NeuralNetwork() {
        edgesWeights = new LinkedList<>();
        Arrays.fill(BIASES, BIAS);
    }

    public double[] calculate(double[] input) {
        SimpleMatrix prevActivations = new SimpleMatrix(input.length, 1, false, input);

        for (int layer = 0; layer < edgesWeights.size(); layer++) {

            SimpleMatrix weights = new SimpleMatrix(edgesWeights.get(layer));
            SimpleMatrix biases = new SimpleMatrix(weights.numRows(), 1, false, BIASES);

            SimpleMatrix current = weights.mult(prevActivations).plus(biases);

            for (int i = 0; i < current.numRows(); i++) {
                current.set(i, 0, activate(current.get(i, 0)));
            }

            prevActivations = current;
        }

        return prevActivations.getDDRM().data;
    }

    public NeuralNetwork copy() {
        NeuralNetwork newNN = new NeuralNetwork();
        this.edgesWeights.forEach(array -> {
            double[][] arrCopy = new double[array.length][];
            for(int i = 0; i < array.length; i++) {
                double[] aMatrix = array[i];
                int size = aMatrix.length;
                arrCopy[i] = new double[size];
                System.arraycopy(aMatrix, 0, arrCopy[i], 0, size);
            }
            newNN.edgesWeights.add(arrCopy);
        });
        return newNN;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder((edgesWeights.size() + 1) + "\n");
        for (int layer = 0; layer < edgesWeights.size(); layer++) {
            for (int i = 0; i < edgesWeights.get(layer).length; i++) {
                for (int j = 0; j < edgesWeights.get(layer)[i].length; j++) {
                    result.append(edgesWeights.get(layer)[i][j]).append(" ");
                }
                result.append("\n");
            }
            result.append("\n");
        }

        return result.toString();
    }

    private static double activate(double x) {
//        return Math.max(0, x);
        return 1 / (1 + Math.exp(-x));
    }
}
