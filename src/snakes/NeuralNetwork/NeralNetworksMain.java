package snakes.NeuralNetwork;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import DKabirov.Bot_D_Kabirov;
import snakes.Bot;
import snakes.SnakesUIMain;

import static snakes.SnakesUIMain.MAZE_SIZE;

public class NeralNetworksMain {

    static Random random = new Random();
    public final static int FIRST_LAYER_NEURONS_NUMBER = (MAZE_SIZE.x * MAZE_SIZE.y - 2) * 2 + 4;

    public static void main(String[] args) throws IOException, InterruptedException {
       Population population = new Population();

        ArrayList<Bot> bots = new ArrayList<>();

        Bot humanWrittenBot = new Bot_D_Kabirov();
        bots.add(null);
        bots.add(humanWrittenBot);

        int generationNumber = 1;
        while (true) {
            long time = System.currentTimeMillis();
            System.out.println("Generation: " + generationNumber + " ");
            population.makeNextGeneration();

            System.out.print(population.bestNN.layers.size() + ": ");
            for (int i = 0; i < population.bestNN.layers.size(); i++) {
                System.out.print(population.bestNN.layers.get(i).biases.size() + " ");
            }
            System.out.println();


            if (generationNumber % 10 == 0) {
                bots.set(0, new Bot_NN(population.bestNN));
                bots.set(1, new Bot_NN(population.secondBestNN));
                SnakesUIMain.start_tournament_n_times(1, bots);
            }

            System.out.println("Time taken: " + (System.currentTimeMillis() - time) / 1000 + " seconds (" + (System.currentTimeMillis() - time) / 60000.0 + " mins)\n");
            generationNumber += 1;
        }
    }
}
