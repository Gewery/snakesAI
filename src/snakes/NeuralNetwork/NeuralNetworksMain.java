package snakes.NeuralNetwork;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import DKabirov.Bot_D_Kabirov;
import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.SnakeGame;

import static snakes.NeuralNetwork.Population.STEPS_PER_GAME;
import static snakes.SnakesUIMain.MAZE_SIZE;

public class NeuralNetworksMain {

    private static final String RESULT_FILE = "result2.txt";
    private static FileWriter resultFw;

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

            String timeTaken = "Time taken: " + (System.currentTimeMillis() - time) / 1000 + " seconds ("
                + (System.currentTimeMillis() - time) / 60000.0 + " mins)\n";
            System.out.println(timeTaken);
            output(
                generationNumber,
                runGame(new Bot_NN(population.bestNN), humanWrittenBot),
                population.bestNN,
                timeTaken,
                (generationNumber % 5 != 0)
            );

//            if (generationNumber % 10 == 0) {
//                bots.set(0, new Bot_NN(population.bestNN));
//                bots.set(1, new Bot_NN(population.secondBestNN));
//                SnakesUIMain.start_tournament_n_times(1, bots);
//            }

            generationNumber += 1;
        }
    }

    static SnakeGame runGame(Bot bot0, Bot bot1) throws InterruptedException {
        Coordinate mazeSize = new Coordinate(14, 14);
        Coordinate head0 = new Coordinate(6, 5);
        Direction tailDirection0 = Direction.DOWN;
        Coordinate head1 = new Coordinate(6, 8);
        Direction tailDirection1 = Direction.UP;
        int snakeSize = 3;

        SnakeGame game =
            new SnakeGame(mazeSize, head0, tailDirection0, head1, tailDirection1, snakeSize, bot0, bot1);
        game.runWithoutPauses(STEPS_PER_GAME);

        return game;
    }

    static void output(int generationNumber, SnakeGame game, NeuralNetwork nn, String additionalInfo, boolean append)
        throws IOException {
        try {
            resultFw = new FileWriter(RESULT_FILE, append);
        } catch (IOException e) {
            e.printStackTrace();
        }

        resultFw.write("Generation " + generationNumber + " (" + (game.gameResult.charAt(0) == '1' ? "Won " : "Lost ")
            + game.appleEaten0 + ")\n");
        resultFw.write(additionalInfo + "\n");
        resultFw.write(nn.toString());
        resultFw.flush();
    }
}
