package snakes.NeuralNetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import DKabirov.Bot_D_Kabirov;
import snakes.Bot;
import snakes.NeuralNetwork.ActivationFunctions.ReLU;
import snakes.NeuralNetwork.ActivationFunctions.Sigmoid;
import snakes.SnakesUIMain;

import static snakes.NeuralNetwork.NeuralNetworksMain.FIRST_LAYER_NEURONS_NUMBER;
import static snakes.NeuralNetwork.Population.DEFAULT_ACTIVATION_FUNCTION;

public class ImportRunNeuralNetworkMain {

    private static final String INPUT_FILE = "input.txt";

    public static void main(String[] args) throws IOException, InterruptedException {

        NeuralNetwork nn = readNN(INPUT_FILE);

        ArrayList<Bot> bots = new ArrayList<>();

        Bot humanWrittenBot = new Bot_D_Kabirov();
        bots.add(new Bot_NN(nn));
        bots.add(humanWrittenBot);

        SnakesUIMain.start_tournament_n_times(2, bots);
    }

    private static NeuralNetwork readNN(String inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE));

        NeuralNetwork result = new NeuralNetwork();

        Layer inputLayer = new Layer();
        for (int i = 0; i < FIRST_LAYER_NEURONS_NUMBER; i++) {
            inputLayer.biases.add(0.0);
            inputLayer.incomingEdges.add(new LinkedList<>());
            inputLayer.activationFunctions.add(DEFAULT_ACTIVATION_FUNCTION);
        }
        result.layers.add(inputLayer);

        String currentLine = reader.readLine();
        int numberOfLayers = Integer.parseInt(currentLine);

        for (int i = 1; i < numberOfLayers; i++) {
            Layer currentLayer = new Layer();

            currentLine = reader.readLine();
            int numberOfNeurons = Integer.parseInt(currentLine);

            currentLine = reader.readLine();
            currentLayer.biases = Arrays.stream(currentLine.split(" ")).map(Double::parseDouble).collect(Collectors.toList());

            currentLine = reader.readLine();
            currentLayer.activationFunctions = Arrays.stream(currentLine.split(" ")).map(af -> af.equals("Sigmoid") ? new Sigmoid() : new ReLU()).collect(Collectors.toList());

            for (int j = 0; j < numberOfNeurons; j++){
                currentLine = reader.readLine();
                currentLayer.incomingEdges.add(Arrays.stream(currentLine.split(" ")).map(Double::parseDouble).collect(Collectors.toList()));
            }
            reader.readLine();

            result.layers.add(currentLayer);
        }

        reader.close();

        return result;
    }
}
