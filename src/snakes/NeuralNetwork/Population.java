package snakes.NeuralNetwork;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javafx.util.Pair;
import snakes.Coordinate;
import snakes.Direction;
import snakes.NeuralNetwork.ActivationFunctions.ActivationFunction;
import snakes.NeuralNetwork.ActivationFunctions.Sigmoid;
import snakes.SnakeGame;

import static snakes.NeuralNetwork.NeuralNetworksMain.FIRST_LAYER_NEURONS_NUMBER;

public class Population {

    public static final int POPULATION_SIZE = 20; // (POPULATION_SIZE - ELITISM_COUNT) % 2 == 0 must hold!
    public static final int ELITISM_COUNT = 4; // must be less or equal to POPULATION_SIZE
    public static final int PARENTS_SELECTION_GROUP_SIZE = 10;

    public static final int MAX_LAYERS_NUMBER = 10; // Limit for the number of layers in initial population
    public static final int MAX_NEURONS_NUMBER = 300; // Limit for the number of neurons in initial population
    public static final int MAX_MUTATION_LAYERS_NUMBER = 5; // max number of layers that can be added to a network after mutation
    public static final ActivationFunction DEFAULT_ACTIVATION_FUNCTION = new Sigmoid();
    public static final int STEPS_PER_GAME = 120; // number of steps allowed for one game = 2 mins = 2 * 60
    public static final int NUMBER_OF_TOURNAMENT_RUNS = 1; //each population runs a tournament this number of times
    public static final double MUTATION_PROBABILITY = 1;
    public static double CROSSOVER_PROBABILITY = 1;


    private static final Random random = new Random();

    List<NeuralNetwork> networks;

    NeuralNetwork bestNN;
    NeuralNetwork secondBestNN;

    public Population() {
        networks = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            networks.add(generateRandomNeuralNetwork());
        }
    }

    public void makeNextGeneration() throws InterruptedException {
        // 0 - run the tournament between old generation's trees
        ArrayList<Pair<NeuralNetwork, Integer>> tournamentResults = runTournamentNTimes(networks, NUMBER_OF_TOURNAMENT_RUNS);
        bestNN = tournamentResults.get(tournamentResults.size() - 1).getKey();
        secondBestNN = tournamentResults.get(tournamentResults.size() - 2).getKey();
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
        if (random.nextDouble() <= MUTATION_PROBABILITY) {
            int chosen = random.nextInt(nextGeneration.size());
            nextGeneration.set(chosen, mutation(nextGeneration.get(chosen)));
        }

        // 4 - elitism stage
        // take ELITISM_COUNT best and copy them into the new generation
        for (int i = 0; i < ELITISM_COUNT; i++) {
            nextGeneration.add(tournamentResults.get(tournamentResults.size() - i - 1).getKey());
        }

        networks = nextGeneration;
    }

    private NeuralNetwork mutation(NeuralNetwork nn) {
        nn = nn.copy();

        Pair<Integer, Integer> range = randomRangeInBounds(1, nn.layers.size() - 1);
        nn.layers.subList(range.getKey(), range.getValue() + 1).clear();

        List<Layer> newLayers = generateRandomLayers(random.nextInt(MAX_MUTATION_LAYERS_NUMBER) + 1, 0);

        nn.layers.addAll(range.getKey(), newLayers);

        adaptToPreviousLayer(nn.layers.get(range.getKey()), nn.layers.get(range.getKey() - 1));
        adaptToPreviousLayer(nn.layers.get(range.getKey() + newLayers.size()), nn.layers.get(range.getKey() + newLayers.size() - 1));

        return nn;
    }

    private List<Layer> generateRandomLayers(int size, int prevLayerSize) {
        LinkedList<Layer> layers = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            Layer layer = new Layer();
            int numberOfNodes = random.nextInt(MAX_NEURONS_NUMBER) + 1;

            for (int currentLayerNode = 0; currentLayerNode < numberOfNodes; currentLayerNode++) {
                layer.biases.add(random.nextDouble());
                layer.incomingEdges.add(new LinkedList<>());
                layer.activationFunctions.add(DEFAULT_ACTIVATION_FUNCTION);
                for (int prevLayerNode = 0; prevLayerNode < prevLayerSize; prevLayerNode++) {
                    layer.incomingEdges.get(currentLayerNode).add(random.nextDouble());
                }
            }
            layers.add(layer);
            prevLayerSize = numberOfNodes;
        }

        return layers;
    }

    private Pair<NeuralNetwork, NeuralNetwork> crossover(NeuralNetwork n1, NeuralNetwork n2) {
        if (n1.layers.size() == 2 || n2.layers.size() == 2) { // if no hidden layers
            return new Pair<>(n1, n2);
        }

        Pair<Integer, Integer> range1 = randomRangeInBounds(1, n1.layers.size() - 1);
        List<Layer> sublist1 = n1.layers.subList(range1.getKey(), range1.getValue() + 1);
        int range1Size = range1.getValue() - range1.getKey() + 1;

        Pair<Integer, Integer> range2 = randomRangeInBounds(1, n2.layers.size() - 1);
        List<Layer> sublist2 = n2.layers.subList(range2.getKey(), range2.getValue() + 1);
        int range2Size = range2.getValue() - range2.getKey() + 1;

        n1.layers.addAll(range1.getKey(), sublist2);
        sublist1 = n1.layers.subList(range1.getKey() + sublist2.size(), range1.getValue() + sublist2.size() + 1);
        n2.layers.addAll(range2.getKey(), sublist1);
        sublist2 = n2.layers.subList(range2.getKey() + sublist1.size(), range2.getValue() + sublist1.size() + 1);

        sublist1.clear();
        sublist2.clear();

        adaptToPreviousLayer(n1.layers.get(range1.getKey() + range2Size), n1.layers.get(range1.getKey() + range2Size - 1));
        adaptToPreviousLayer(n1.layers.get(range1.getKey()), n1.layers.get(range1.getKey() - 1));

        adaptToPreviousLayer(n2.layers.get(range2.getKey() + range1Size), n2.layers.get(range2.getKey() + range1Size - 1));
        adaptToPreviousLayer(n2.layers.get(range2.getKey()), n2.layers.get(range2.getKey() - 1));

        return new Pair<>(n1, n2);
    }

    private void adaptToPreviousLayer(Layer currentLayer, Layer previousLayer) {
        int currentEdgesSize = currentLayer.incomingEdges.get(0).size();
        int previousLayerSize = previousLayer.biases.size();

        if (currentEdgesSize < previousLayerSize) { // add random edges
            for (List<Double> innerIncomingEdges : currentLayer.incomingEdges) {
                for (int j = 0; j < previousLayerSize - currentEdgesSize; j++) {
                    innerIncomingEdges.add(random.nextDouble());
                }
            }
        } else if (currentEdgesSize > previousLayerSize) { // delete random edges
            for (List<Double> innerIncomingEdges : currentLayer.incomingEdges) {
                for (int j = 0; j < currentEdgesSize - previousLayerSize; j++) {
                    innerIncomingEdges.remove(random.nextInt(innerIncomingEdges.size()));
                }
            }
        }
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

    /**
     * Runs a tournament between trees specified number of times
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

    private NeuralNetwork generateRandomNeuralNetwork() {
        NeuralNetwork nn = new NeuralNetwork();
        int layersCount = random.nextInt(MAX_LAYERS_NUMBER) + 2;

        // add input layer
        Layer inputLayer = new Layer();
        for (int i = 0; i < FIRST_LAYER_NEURONS_NUMBER; i++) {
            inputLayer.biases.add(0.0);
            inputLayer.incomingEdges.add(new LinkedList<>());
            inputLayer.activationFunctions.add(DEFAULT_ACTIVATION_FUNCTION);
        }
        nn.layers.add(inputLayer);

        // add hidden layers
        nn.layers.addAll(generateRandomLayers(Math.max(0, layersCount - 2), inputLayer.biases.size()));

        // add output layer
        Layer outputLayer = new Layer();
        int prevLayerSize = nn.layers.get(nn.layers.size() - 1).biases.size();
        for (int currentLayerNode = 0; currentLayerNode < 4; currentLayerNode++) {
            outputLayer.biases.add(random.nextDouble());
            outputLayer.incomingEdges.add(new LinkedList<>());
            outputLayer.activationFunctions.add(DEFAULT_ACTIVATION_FUNCTION);

            for (int prevLayerNode = 0; prevLayerNode < prevLayerSize; prevLayerNode++) {
                outputLayer.incomingEdges.get(currentLayerNode).add(random.nextDouble());
            }
        }
        nn.layers.add(outputLayer);

        return nn;
    }
}
