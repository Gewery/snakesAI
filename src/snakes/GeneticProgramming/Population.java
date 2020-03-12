package snakes.GeneticProgramming;

import javafx.util.Pair;
import snakes.Coordinate;
import snakes.Direction;
import snakes.SnakeGame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Population {
    final int POPULATION_SIZE = 10; // (POPULATION_SIZE - ELITISM_COUNT) % 2 == 0 must hold!
    final int ELITISM_COUNT = 2; // must be less or equal to POPULATION_SIZE
    final int PARENTS_SELECTION_GROUP_SIZE = 5;
    final int MAX_MUTATION_HEIGHT_SUBTREE = 5; // max height that can be added to a tree after mutation
    final int NUMBER_OF_TOURNAMENT_RUNS = 1;
    final int STEPS_PER_GAME = 900; // number of steps allowed for one game
    final double MUTATION_PROBABILITY = 0.03;
    final double CROSSOVER_PROBABILITY = 0.5;

    ArrayList<Node> trees = new ArrayList<>();
    Random rn = new Random();

    /**
     * Initialize population with random trees
     */
    public Population() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Node newTree = new Node(null);
            newTree.generateSubtree(MAX_MUTATION_HEIGHT_SUBTREE);
            trees.add(newTree);
        }
    }

    /**
     * Inits whole population by copies of initBy tree
     * @param initBy root node of a tree that will be copied
     */

    public Population(Node initBy) {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            trees.add(copyTree(initBy));
        }
    }

    public void makeNextGeneration() throws InterruptedException {
        ArrayList<Node> nextGeneration = new ArrayList<>();
        // crossover stage
        for (int i = 0; i < POPULATION_SIZE - ELITISM_COUNT; i += 2) {
            Node par1 = null, par2 = null;
            int par1_index = -1, par2_index = -1;
            for (int j = 0; j < 2; j++) {
                ArrayList<Node> parentsSelectionGroup = new ArrayList<>();
                ArrayList<Integer> parentsSelectionGroupIndices = new ArrayList<>();
                for (int k = 0; k < PARENTS_SELECTION_GROUP_SIZE; k++) {
                    int chosen = rn.nextInt(POPULATION_SIZE);
                    while (chosen == par1_index) { // to make sure par2 != par1
                        chosen = rn.nextInt(POPULATION_SIZE);
                    }
                    parentsSelectionGroup.add(trees.get(chosen));
                    parentsSelectionGroupIndices.add(chosen);
                }

                ArrayList<Pair<Node, Integer>> tournamentResults = runTournamentNTimes(parentsSelectionGroup, NUMBER_OF_TOURNAMENT_RUNS);
                int best_tree_index = -1;
                int max_score = -1;
                for (int k = 0; k < tournamentResults.size(); k++) { // choosing the best Node
                    if (tournamentResults.get(k).getValue() > max_score) {
                        max_score = tournamentResults.get(k).getValue();
                        best_tree_index = parentsSelectionGroupIndices.get(k);
                    }
                }
                if (par1_index == -1) {
                    par1_index = best_tree_index;
                    par1 = trees.get(par1_index);
                } else {
                    par2_index = best_tree_index;
                    par2 = trees.get(par2_index);
                }
            }

            Pair<Node, Node> children;
            if (rn.nextDouble() <= CROSSOVER_PROBABILITY) {
                children = crossover(copyTree(par1), copyTree(par2));
            }
            else {
                children = new Pair<>(par1, par2);
            }
            nextGeneration.add(children.getKey());
            nextGeneration.add(children.getValue());
        }


        // mutation stage
        if (rn.nextDouble() <= MUTATION_PROBABILITY) {
            int chosen = rn.nextInt(nextGeneration.size());
            nextGeneration.set(chosen, mutation(nextGeneration.get(chosen)));
        }

        // elitism stage
        // 0 - run the tournament between old generation's trees
        ArrayList<Pair<Node, Integer>> tournamentResults = runTournamentNTimes(trees, NUMBER_OF_TOURNAMENT_RUNS);
        // 1 - sort trees
        tournamentResults.sort(Comparator.comparingInt(Pair::getValue)); // sort by the number of wins
        // take ELITISM best and copy them into the new generation
        for (int i = 0; i < ELITISM_COUNT; i++) {
            nextGeneration.add(tournamentResults.get(tournamentResults.size() - i - 1).getKey());
        }
    }

    /**
     * Runs a tournament between trees specified number of times
     * @param participantsTrees ArrayList with root-Nodes of trees that will participate in the tournament
     * @param n number of tournament runs
     * @return ArrayList with pairs (Node, it's score) !Order of nodes is the same as in participantsTrees!
     * @throws InterruptedException
     */
    private ArrayList<Pair<Node, Integer>> runTournamentNTimes(ArrayList<Node> participantsTrees, int n) throws InterruptedException {
        // Initial game settings
        Coordinate mazeSize = new Coordinate(14, 14);
        Coordinate head0 = new Coordinate(5, 5);
        Direction tailDirection0 = Direction.DOWN;
        Coordinate head1 = new Coordinate(8, 8);
        Direction tailDirection1 = Direction.UP;
        int snakeSize = 3;

        int[] tournamentResults = new int[participantsTrees.size() + 1];

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < participantsTrees.size(); i++)
                for (int j = i + 1; j < participantsTrees.size(); j++) {
                    int bot0ind = i;
                    int bot1ind = j;
                    if (rn.nextInt(2) == 0) { // swap them sometimes for getting more reliable results
                        int t = bot0ind;
                        bot0ind = bot1ind;
                        bot1ind = t;
                    }

                    Bot_GP bot0 = new Bot_GP(participantsTrees.get(bot0ind));
                    Bot_GP bot1 = new Bot_GP(participantsTrees.get(bot1ind));
                    SnakeGame game = new SnakeGame(mazeSize, head0, tailDirection0, head1, tailDirection1, snakeSize, bot0, bot1);
                    game.runWithoutPauses(STEPS_PER_GAME);
                    tournamentResults[bot0ind] += Integer.parseInt(game.gameResult.substring(0, 1));
                    tournamentResults[bot1ind] += Integer.parseInt(game.gameResult.substring(game.gameResult.length() - 1));
                }
        }

        ArrayList<Pair<Node, Integer>> results = new ArrayList<>();
        for (int i = 0; i < participantsTrees.size(); i++)
            results.add(new Pair<>(participantsTrees.get(i), tournamentResults[i]));

        return results;
    }

    /**
     * Cross two trees:
     * Choose random subtree in the first tree and exchange it with a random subtree in the second tree
     * Returns both children
     * @param a first parent
     * @param b second parent
     * @return pair of children tree root nodes
     */
    private Pair<Node, Node> crossover(Node a, Node b) {
        ArrayList<Node> nodesA = new ArrayList<>();
        ArrayList<Node> nodesB = new ArrayList<>();
        dfs(a, nodesA); // place all the nodes of 'a' into the nodesA
        dfs(b, nodesB); // place all the nodes of 'b' into the nodesB
        Node chosenSubtreeA = nodesA.get(rn.nextInt(nodesA.size())); // choose random node
        Node chosenSubtreeB = nodesB.get(rn.nextInt(nodesB.size()));

        // Exchanging chosenSubtreeA with chosenSubtreeB

        if (chosenSubtreeA.parent != null)
            chosenSubtreeA.parent.changeChild(chosenSubtreeA, chosenSubtreeB); // change parent's reference to the new child
        else
            a = chosenSubtreeB; // if there's no parent - chosenSubtreeB became a new first root

        if (chosenSubtreeB.parent != null)
            chosenSubtreeB.parent.changeChild(chosenSubtreeB, chosenSubtreeA); // change parent's reference to the new child
        else
            b = chosenSubtreeA; // if there's no parent - chosenSubtreeA became a new second root

        Node temp = chosenSubtreeA.parent;
        chosenSubtreeA.parent = chosenSubtreeB.parent;
        chosenSubtreeB.parent = temp;

        return new Pair<>(a, b); // return both children
    }

    /**
     * Make a copy of the tree
     * @param a root node
     * @return root node of a copied tree
     */
    private Node copyTree(Node a) {
        if (a == null)
            return null;
        return new Node(a, copyTree(a.left), copyTree(a.right));
    }

    /**
     * Apply mutation for tree:
     * Take a random Node and regenerate it and its subtree
     * @param a root node of a tree
     * @return mutated copy of param a
     */
    private Node mutation(Node a) {
        ArrayList<Node> nodes = new ArrayList<>();
        dfs(a, nodes);
        Node chosenNode = nodes.get(rn.nextInt(nodes.size())); // take random node

        Node newNode = new Node(chosenNode.parent); // create a new Node
        newNode.generateSubtree(MAX_MUTATION_HEIGHT_SUBTREE); // generate its subtree

        if (chosenNode.parent != null)
            chosenNode.parent.changeChild(chosenNode, newNode); // change parent's reference to new child
        else
            a = newNode; // if chosenNode was a root - whole tree was regenerated

        return a;
    }

    /**
     * Traverse a tree and make a list of all nodes
     * @param t current node
     * @param tree_nodes ArrayList for storing nodes
     */
    private void dfs(Node t, ArrayList<Node> tree_nodes) {
        tree_nodes.add(t);
        if (t.left != null)
            dfs(t.left, tree_nodes);
        if (t.right != null)
            dfs(t.right, tree_nodes);
    }
}
