package snakes.simpleNeuralNetwork;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javafx.util.Pair;
import snakes.Coordinate;
import snakes.Direction;
import snakes.SnakeGame;

import static snakes.SnakesUIMain.MAZE_SIZE;

public class Population {

    public static final int POPULATION_SIZE = 200; // (POPULATION_SIZE - ELITISM_COUNT) % 2 == 0 must hold!
    public static final int ELITISM_COUNT = 40; // must be less or equal to POPULATION_SIZE
    public static final int PARENTS_SELECTION_GROUP_SIZE = 80;

    public static final int MAX_LAYERS_NUMBER = 10; // Limit for the number of layers in initial population
    public static final int NEURONS_IN_LAYER = 200; // Number of neurons in one hidden layer
    public static final double MUTATION_PERCENT = 0.05; // percent of weights that will be changed during the mutation
    public static final int STEPS_PER_GAME = 120; // number of steps allowed for one game = 2 mins = 2 * 60
    public static final int NUMBER_OF_TOURNAMENT_RUNS = 1; //each population runs a tournament this number of times
    public static final double MUTATION_PROBABILITY = 0.02;
    public static double CROSSOVER_PROBABILITY = 1;

    public final static int FIRST_LAYER_NEURONS_NUMBER = (MAZE_SIZE.x * MAZE_SIZE.y - 2) * 4 + 2;
    public final static int LAST_LAYER_NEURONS_NUMBER = 4;
    public NeuralNetwork bestNN;

    private static final Random random = new Random();

    private List<NeuralNetwork> networks;

    public Population() {
        networks = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            networks.add(generateRandomNeuralNetwork());
        }
    }

    void makeNextGeneration() throws InterruptedException {
        // 0 - run the tournament between old generation's trees
        ArrayList<Pair<NeuralNetwork, Integer>> tournamentResults = runTournamentNTimes(networks, NUMBER_OF_TOURNAMENT_RUNS);
        bestNN = tournamentResults.get(tournamentResults.size() - 1).getKey();
        // 1 - sort networks
        tournamentResults.sort(Comparator.comparingInt(Pair::getValue)); // sort by the number of wins

        // 2 - crossover stage
        ArrayList<NeuralNetwork> nextGeneration = new ArrayList<>();
        // 2 - crossover stage
        for (int i = 0; i < POPULATION_SIZE - ELITISM_COUNT; i += 2) {
            int max1 = -1, max2 = -1;
            for (int j = 0; j < PARENTS_SELECTION_GROUP_SIZE; j++) {
                int randInd = random.nextInt(POPULATION_SIZE);
                if (randInd > max1) {
                    max2 = max1;
                    max1 = randInd;
                } else if (randInd > max2) {
                    max2 = randInd;
                }
            }
            NeuralNetwork par1 = tournamentResults.get(max1).getKey();
            NeuralNetwork par2 = tournamentResults.get(max2).getKey();

            Pair<NeuralNetwork, NeuralNetwork> children;
            if (random.nextDouble() <= CROSSOVER_PROBABILITY) {
                children = crossover(par1.copy(), par2.copy());
            } else {
                children = new Pair<>(par1, par2);
            }
            nextGeneration.add(children.getKey());
            nextGeneration.add(children.getValue());
        }


        // 3 - mutation stage
        for (int i = 0; i < nextGeneration.size(); i++) {
            if (random.nextDouble() <= MUTATION_PROBABILITY) {
                nextGeneration.set(i, mutation(nextGeneration.get(i)));
            }
        }

        // 4 - elitism stage
        // take ELITISM_COUNT best and copy them into the new generation
        for (int i = 0; i < ELITISM_COUNT; i++) {
            nextGeneration.add(tournamentResults.get(tournamentResults.size() - i - 1).getKey());
        }

        networks = nextGeneration;
    }


    private Pair<NeuralNetwork, NeuralNetwork> crossover(NeuralNetwork n1, NeuralNetwork n2) {
        if (n1.edgesWeights.size() <= 2 || n2.edgesWeights.size() <= 2) {
            return new Pair<>(n1, n2);
        }
        Pair<Integer, Integer> range1 = randomRangeInBounds(1, n1.edgesWeights.size() - 1);
        List<double[][]> sublist1 = n1.edgesWeights.subList(range1.getKey(), range1.getValue() + 1);

        Pair<Integer, Integer> range2 = randomRangeInBounds(1, n2.edgesWeights.size() - 1);
        List<double[][]> sublist2 = n2.edgesWeights.subList(range2.getKey(), range2.getValue() + 1);

        n1.edgesWeights.addAll(range1.getKey(), sublist2);
        sublist1 = n1.edgesWeights.subList(range1.getKey() + sublist2.size(), range1.getValue() + sublist2.size() + 1);
        n2.edgesWeights.addAll(range2.getKey(), sublist1);
        sublist2 = n2.edgesWeights.subList(range2.getKey() + sublist1.size(), range2.getValue() + sublist1.size() + 1);

        sublist1.clear();
        sublist2.clear();

        return new Pair<>(n1, n2);
    }

    private NeuralNetwork mutation(NeuralNetwork nn) {
        nn = nn.copy();

        int totalEdges = 0;
        for (int i = 0; i < nn.edgesWeights.size(); i++) {
            totalEdges += nn.edgesWeights.get(i).length * nn.edgesWeights.get(i)[0].length;
        }

        int mutationCount = Double.valueOf(Math.ceil(MUTATION_PERCENT * totalEdges)).intValue();

        for (int i = 0; i < mutationCount; i++) {
            int layer = random.nextInt(nn.edgesWeights.size());
            int row = random.nextInt(nn.edgesWeights.get(layer).length);
            int column = random.nextInt(nn.edgesWeights.get(layer)[row].length);

            nn.edgesWeights.get(layer)[row][column] = random.nextDouble() * (random.nextInt(2) == 0 ? 1 : -1);
        }

        return nn;
    }

    private Pair<Integer, Integer> randomRangeInBounds(int lowInclusive, int highExclusive) {
        int beginIndex = randomInBounds(lowInclusive, highExclusive);
        int endIndex = randomInBounds(lowInclusive, highExclusive);

        if (beginIndex > endIndex) {
            int temp = beginIndex;
            beginIndex = endIndex;
            endIndex = temp;
        }

        return new Pair<>(beginIndex, endIndex);
    }

    private int randomInBounds(int lowInclusive, int highExclusive) {
        return random.nextInt(highExclusive - lowInclusive) + lowInclusive;
    }

    private NeuralNetwork generateRandomNeuralNetwork() {
        NeuralNetwork neuralNetwork = new NeuralNetwork();

        int layersCount = random.nextInt(MAX_LAYERS_NUMBER) + 2; // at least 1 hidden layer

        int previousLayerSize = FIRST_LAYER_NEURONS_NUMBER;
        for (int i = 0; i < layersCount; i++) {
            int nextLayerSize = NEURONS_IN_LAYER;
            if (i == layersCount - 1) {
                nextLayerSize = LAST_LAYER_NEURONS_NUMBER;
            }

            neuralNetwork.edgesWeights.add(generateRandomLayer(previousLayerSize, nextLayerSize));
            previousLayerSize = nextLayerSize;
        }

        return neuralNetwork;
    }

    private double[][] generateRandomLayer(int previousLayerSize, int nextLayerSize) {
        double[][] currentEdges = new double[nextLayerSize][previousLayerSize];
        for (int prevLayerIndex = 0; prevLayerIndex < previousLayerSize; prevLayerIndex++) {
            for (int nextLayerIndex = 0; nextLayerIndex < nextLayerSize; nextLayerIndex++) {
                currentEdges[nextLayerIndex][prevLayerIndex ] = random.nextDouble() * (random.nextInt(2) == 0 ? 1 : -1);
            }
        }

        return currentEdges;
    }

    /**
     * Runs a tournament between bots specified number of times
     *
     * @param participantsNetworks ArrayList with Networks that will participate in the tournament
     * @param n                 number of tournament runs
     * @return ArrayList with pairs (Node, it's score) !Order of nodes is the same as in participantsTrees!
     * @throws InterruptedException
     */
    public ArrayList<Pair<NeuralNetwork, Integer>> runTournamentNTimes(List<NeuralNetwork> participantsNetworks, int n)
        throws InterruptedException {
        // Initial game settings
        Coordinate mazeSize = new Coordinate(14, 14);
        Coordinate head0 = new Coordinate(6, 5);
        Direction tailDirection0 = Direction.DOWN;
        Coordinate head1 = new Coordinate(6, 8);
        Direction tailDirection1 = Direction.UP;
        int snakeSize = 3;

        int[] tournamentResults = new int[participantsNetworks.size() + 1];

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < participantsNetworks.size(); i++) {
                for (int j = i + 1; j < participantsNetworks.size(); j++) {
                    int bot0ind = i;
                    int bot1ind = j;
                    if (random.nextInt(2) == 0) { // swap them sometimes for getting more reliable results
                        int t = bot0ind;
                        bot0ind = bot1ind;
                        bot1ind = t;
                    }

                    Bot_NN bot0 = new Bot_NN(participantsNetworks.get(bot0ind));
                    Bot_NN bot1 = new Bot_NN(participantsNetworks.get(bot1ind));
                    SnakeGame game =
                        new SnakeGame(mazeSize, head0, tailDirection0, head1, tailDirection1, snakeSize, bot0, bot1);
                    game.runWithoutPauses(STEPS_PER_GAME);
                    // score = (win ? 1 : 0) * 1000 + applesEaten
                    tournamentResults[bot0ind] += 1000 * Integer.parseInt(game.gameResult.substring(0, 1));
                    tournamentResults[bot1ind] +=
                        1000 * Integer.parseInt(game.gameResult.substring(game.gameResult.length() - 1));
                    tournamentResults[bot0ind] += game.appleEaten0;
                    tournamentResults[bot1ind] += game.appleEaten1;
                }
            }
        }

        ArrayList<Pair<NeuralNetwork, Integer>> results = new ArrayList<>();
        for (int i = 0; i < participantsNetworks.size(); i++) {
            results.add(new Pair<>(participantsNetworks.get(i), tournamentResults[i]));
        }

        return results;
    }
}
