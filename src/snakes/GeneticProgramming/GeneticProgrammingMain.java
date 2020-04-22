package snakes.GeneticProgramming;

import DKabirov.Bot_D_Kabirov;
import NStrygin.Bot_n_strygin;
import javafx.util.Pair;
import snakes.*;
import snakes.GeneticProgramming.Operations.Subtraction;
import snakes.GeneticProgramming.Subfunctions.CollisionWithObject;
import snakes.GeneticProgramming.Subfunctions.ManhattanDistanceApple;

import java.util.ArrayList;
import java.util.Comparator;

public class GeneticProgrammingMain {
    final static int MAX_GENERATION_COUNT = 200;
// NOTE: fitness function (or score) = (number of won games) * 10 + apples eaten
    public static void main(String[] args) throws InterruptedException {
        Node root = new Node(2, null);
        root.operation = new Subtraction();
        root.left = new Node(1, root);
        root.right = new Node(1, root);
        root.right.subfunction = new ManhattanDistanceApple();
        root.left.subfunction = new CollisionWithObject();
        Population population = new Population();
        //population.trees.add(root);
        for (int i = 0; i < MAX_GENERATION_COUNT; i++) {
            long time = System.currentTimeMillis();
            System.out.println("Generation: " + i + " ");
            Node best_prev_gen = population.makeNextGeneration();

            printTree(best_prev_gen);

            System.out.println("Time taken: " + (System.currentTimeMillis() - time) / 1000 + " seconds\n");
        }

        ArrayList<Pair<Node, Integer>> tournamentResults = population.runTournamentNTimes(population.trees, population.NUMBER_OF_TOURNAMENT_RUNS);
        tournamentResults.sort(Comparator.comparingInt(Pair::getValue)); // sort by the number of wins
        Node bestTree = tournamentResults.get(tournamentResults.size() - 1).getKey(); // find the best tree
        printTree(bestTree);

        //new java.util.Scanner(System.in).nextLine();

        // Play the best tree vs Bot_D_Kabirov() 3 times:

        Bot_GP bot0 = new Bot_GP(bestTree);
        Bot bot1 = new Bot_D_Kabirov();

        Coordinate mazeSize = new Coordinate(14, 14);
        Coordinate head0 = new Coordinate(6, 5);
        Direction tailDirection0 = Direction.DOWN;
        Coordinate head1 = new Coordinate(6, 8);
        Direction tailDirection1 = Direction.UP;
        int snakeSize = 3;

        for (int i = 0; i < 3; i++) {
            SnakeGame game = new SnakeGame(mazeSize, head0, tailDirection0, head1, tailDirection1, snakeSize, bot0, bot1);
            SnakesWindow window = new SnakesWindow(game);
            Thread t = new Thread(window);
            t.start();
            t.join();

            Thread.sleep(1000); // to allow users see the result
            window.closeWindow();
            System.out.println(game.gameResult);
        }
    }

    static void printTree(Node t) {
        switch (t.type) {
            case 0: System.out.println("constant_value: " + t.constant_value); break;
            case 1: System.out.println("subfunction: " + t.subfunction.getClass()); break;
            case 2: System.out.println("operation: " + t.operation.getClass()); printTree(t.left); printTree(t.right); break;
        }
    }
}
