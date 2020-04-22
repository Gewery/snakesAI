package snakes.GeneticProgramming;

import DKabirov.Bot_D_Kabirov;
import NStrygin.Bot_n_strygin;
import javafx.util.Pair;
import snakes.*;
import snakes.GeneticProgramming.Operations.Subtraction;
import snakes.GeneticProgramming.Subfunctions.CollisionWithObject;
import snakes.GeneticProgramming.Subfunctions.ManhattanDistanceApple;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class GeneticProgrammingMain {
    final static int MAX_GENERATION_COUNT = 200;
    private static FileWriter output;

// NOTE: fitness function (or score) = (number of won games) * 10 + apples eaten
    public static void main(String[] args) throws InterruptedException, IOException {
        Population population = new Population();

        output = new FileWriter("output.txt", false);
        output.write("POPULATION_SIZE = " + population.POPULATION_SIZE + " ELITISM_COUNT = " + population.ELITISM_COUNT + " PARENTS_SELECTION_GROUP_SIZE = " + population.PARENTS_SELECTION_GROUP_SIZE + "\n\n");
        output.close();

        int generationNumber = 0;
        while (true) {
            output = new FileWriter("output.txt", true);
            long time = System.currentTimeMillis();
            output.write("Generation: " + generationNumber + "\n");
            //System.out.println("Generation: " + generationNumber + " ");
            generationNumber += 1;
            Node best_prev_gen = population.makeNextGeneration();

            printTree(best_prev_gen);

            output.write("Time taken: " + (System.currentTimeMillis() - time) / 1000 + " seconds\n\n");
            //System.out.println("Time taken: " + (System.currentTimeMillis() - time) / 1000 + " seconds\n");
            output.close();
        }

//        ArrayList<Pair<Node, Integer>> tournamentResults = population.runTournamentNTimes(population.trees, population.NUMBER_OF_TOURNAMENT_RUNS);
//        tournamentResults.sort(Comparator.comparingInt(Pair::getValue)); // sort by the number of wins
//        Node bestTree = tournamentResults.get(tournamentResults.size() - 1).getKey(); // find the best tree
//        printTree(bestTree);
    }

    static void printTree(Node t) throws IOException {
        switch (t.type) {
            case 0: output.write("constant_value: " + t.constant_value + "\n"); break;
            case 1: output.write("subfunction: " + t.subfunction.getClass() + "\n"); break;
            case 2: output.write("operation: " + t.operation.getClass() + "\n"); printTree(t.left); printTree(t.right); break;
        }
    }
}
