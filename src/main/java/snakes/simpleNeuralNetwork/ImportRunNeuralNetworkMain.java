package snakes.simpleNeuralNetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import DKabirov.Bot_D_Kabirov;
import snakes.Bot;
import snakes.SnakesUIMain;

import static snakes.simpleNeuralNetwork.Population.FIRST_LAYER_NEURONS_NUMBER;

public class ImportRunNeuralNetworkMain {

    private static final String INPUT_FILE = "simpleNNresult-yandex.txt";

    public static void main(String[] args) throws IOException, InterruptedException {

        NeuralNetwork nn = readNN();

        ArrayList<Bot> bots = new ArrayList<>();

        Bot humanWrittenBot = new Bot_D_Kabirov();
        bots.add(new Bot_NN(nn));
        bots.add(humanWrittenBot);

        SnakesUIMain.start_tournament_n_times(2, bots);
    }

    private static NeuralNetwork readNN() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE));

        String currentLine = reader.readLine();
        int numberOfLayers = Integer.parseInt(currentLine);

        NeuralNetwork result = new NeuralNetwork();

        int prevLayerSize = FIRST_LAYER_NEURONS_NUMBER;
        for (int i = 0; i < numberOfLayers - 1; i++) {
            currentLine = reader.readLine();
            int nextLayerSize = Population.NEURONS_IN_LAYER;
            if (i == numberOfLayers - 2)
                nextLayerSize = 4;
            double[][] inp = new double[nextLayerSize][prevLayerSize];

            for (int j = 0; j < nextLayerSize; j++) {
                currentLine = reader.readLine();
                List<Double> lst = Arrays.stream(currentLine.split(" ")).map(Double::parseDouble).collect(Collectors.toList());
                for (int ind = 0; ind < prevLayerSize; ind++) {
                    inp[j][ind] = lst.get(ind);
                }
            }

            result.edgesWeights.add(inp);
            prevLayerSize = nextLayerSize;
        }

        reader.close();

        return result;
    }
}
