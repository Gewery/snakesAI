package snakes.GeneticProgramming;

import DKabirov.Bot_D_Kabirov;
import snakes.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GeneticProgrammingMain {
    final static int MAX_GENERATION_COUNT = 200;
    private static FileWriter output;

// NOTE: fitness function (or score) = (number of won games) * 1000 + apples eaten
    public static void main(String[] args) throws InterruptedException, IOException {
        Population population = new Population();

        output = new FileWriter("output.txt", false);
        output.write("POPULATION_SIZE = " + population.POPULATION_SIZE + " ELITISM_COUNT = " + population.ELITISM_COUNT + " PARENTS_SELECTION_GROUP_SIZE = " + population.PARENTS_SELECTION_GROUP_SIZE + "\n\n");
        output.close();

        ArrayList<Bot> bots = new ArrayList<>();

        Bot humanWrittenBot = new Bot_D_Kabirov();
        bots.add(null);
        bots.add(humanWrittenBot);

        int generationNumber = 1;
        while (true) {
            output = new FileWriter("output.txt", true);
            long time = System.currentTimeMillis();
            output.write("Generation: " + generationNumber + "\n");
            System.out.println("Generation: " + generationNumber + " ");
            Node best_prev_gen = population.makeNextGeneration();

            printTree(best_prev_gen, 0);

            if (generationNumber % 10 == 0) {
                bots.set(0, new Bot_GP(best_prev_gen));
                bots.set(1, humanWrittenBot);
                SnakesUIMain.start_tournament_n_times(2, bots);
                output.write("\n" + bots.get(0).getClass().getSimpleName() + " vs. " + bots.get(1).getClass().getSimpleName() + ": " + SnakesUIMain.total_results_table[0][1] + " - " + SnakesUIMain.total_results_table[1][0] + "\n");
            }

            output.write("Time taken: " + (System.currentTimeMillis() - time) / 1000 + " seconds\n\n");
            System.out.println("Time taken: " + (System.currentTimeMillis() - time) / 1000 + " seconds (" + (System.currentTimeMillis() - time) / 60000.0 + " mins)\n");
            output.close();
            generationNumber += 1;
        }

//        ArrayList<Pair<Node, Integer>> tournamentResults = population.runTournamentNTimes(population.trees, population.NUMBER_OF_TOURNAMENT_RUNS);
//        tournamentResults.sort(Comparator.comparingInt(Pair::getValue)); // sort by the number of wins
//        Node bestTree = tournamentResults.get(tournamentResults.size() - 1).getKey(); // find the best tree
//        printTree(bestTree);
    }

    public static void printTree(Node t, int spaces) throws IOException {
        for (int i = 0; i < spaces; i++)
            output.write(' ');
        switch (t.type) {
//            case 0: output.write("constant_value: " + t.constantValue + "\n"); break;
            case 1: output.write("const * subfunction: " + t.constantValue + " * " + t.subfunction.getClass().getSimpleName() + "\n"); break;
            case 2: output.write("operation: " + t.operation.getClass().getSimpleName() + "\n"); printTree(t.left, spaces + 4); printTree(t.right, spaces + 4); break;
        }
    }
}
