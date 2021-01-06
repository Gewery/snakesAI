package snakes.NeuralNetwork;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import DKabirov.Bot_D_Kabirov;
import snakes.NeuralNetwork.ActivationFunctions.Sigmoid;
import snakes.SnakesUIMain;

public class NeralNetworksMain {

    static Random random = new Random();
    final static int NUMBER_OF_LAYERS = 4;
    final static int FIRST_LAYER_NEURONS_NUMBER = 8;
    final static int[] NUMBERS_OF_NEURONS = new int[]{FIRST_LAYER_NEURONS_NUMBER, 10, 10, 4};

    public static void main(String[] args) throws IOException, InterruptedException {


        for (int u = 0; u < 10; u++) {
            NeuralNetwork neuralNetwork = new NeuralNetwork();
            for (int layerInd = 0; layerInd < NUMBER_OF_LAYERS; layerInd++) {
                Layer layer = new Layer();
                for (int currentNodeInd = 0; currentNodeInd < NUMBERS_OF_NEURONS[layerInd]; currentNodeInd++) {
                    layer.biases.add(random.nextDouble());
                    layer.activationFunctions.add(new Sigmoid());
                    layer.incomingEdges.add(new LinkedList<>());

                    if (layerInd != 0) {
                        for (int prevNodeInd = 0; prevNodeInd < NUMBERS_OF_NEURONS[layerInd - 1]; prevNodeInd++) {
                            layer.incomingEdges.get(currentNodeInd).add(random.nextDouble());
                        }
                    }
                }

                neuralNetwork.layers.add(layer);
            }

            SnakesUIMain.start_tournament_n_times(2, Arrays.asList(new Bot_NN(neuralNetwork), new Bot_D_Kabirov()));
        }

    }
}
