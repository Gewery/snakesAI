package snakes.GeneticProgramming;

import javafx.util.Pair;
import snakes.Coordinate;
import snakes.Direction;
import snakes.SnakeGame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Population {
    final int POPULATION_SIZE = 10;
    final int CROSSOVER_COUNT = 5;
    final int SELECTION_GROUP_SIZE = 5;
    final int MAX_MUTATION_HEIGHT_SUBTREE = 5; // max height that can be added to a tree after mutation
    final int MUTATION_COUNT = 5;
    final int NUMBER_OF_TOURNAMENT_RUNS = 1;
    final int STEPS_PER_GAME = 900; // number of steps allowed for one game
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

    public Population(Node initBy) {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            trees.add(copyTree(initBy));
        }
    }

    public void makeNextGeneration() throws InterruptedException {
        // crossover stage
        for (int i = 0; i < CROSSOVER_COUNT/* - 2*/; i++) {
            Node par1 = null, par2 = null;
            int par1_index = -1, par2_index = -1;
            for (int j = 0; j < 2; j++) {
                int best_tree_index = -1;
                for (int k = 0; k < SELECTION_GROUP_SIZE; k++) {
                    int chosen = rn.nextInt(POPULATION_SIZE);
                    while (chosen == par1_index) { // to make sure par2 != par1
                        chosen = rn.nextInt(POPULATION_SIZE);
                    }
                    if (best_tree_index < chosen) {
                        best_tree_index = chosen;
                    }
                }
                if (par1_index == -1) {
                    par1_index = best_tree_index;
                    par1 = trees.get(par1_index);
                }
                else {
                    par2_index = best_tree_index;
                    par2 = trees.get(par2_index);
                }
            }

            Node child = crossover(copyTree(par1), copyTree(par2));
            trees.add(child);
        }

        // mutation stage
        for (int i = 0; i < MUTATION_COUNT; i++) {
            int chosen = rn.nextInt(POPULATION_SIZE);
            trees.add(mutation(copyTree(trees.get(chosen))));
        }

        // selection stage:
        // 0 - run the tournament between each other
        ArrayList<Pair<Node, Integer>> tournamentResults = runTournamentNTimes(NUMBER_OF_TOURNAMENT_RUNS);
        // 1 - sort trees
        tournamentResults.sort(Comparator.comparingInt(Pair::getValue)); // sort by the number of wins
        // 2 - remove worst trees, leave only POPULATION_SIZE best
        for (int i = 0; i < tournamentResults.size() - POPULATION_SIZE; i++)
            trees.remove(tournamentResults.get(i).getKey());
    }

    private ArrayList<Pair<Node, Integer>> runTournamentNTimes(int n) throws InterruptedException {
        // Initial game settings
        Coordinate mazeSize = new Coordinate(14, 14);
        Coordinate head0 = new Coordinate(2, 2);
        Direction tailDirection0 = Direction.DOWN;
        Coordinate head1 = new Coordinate(5, 5);
        Direction tailDirection1 = Direction.UP;
        int snakeSize = 3;

        int[] tournamentResults = new int[trees.size() + 1];

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < trees.size(); i++)
                for (int j = i + 1; j < trees.size(); j++) {
                    Bot_GP bot0 = new Bot_GP(trees.get(i));
                    Bot_GP bot1 = new Bot_GP(trees.get(j));
                    SnakeGame game = new SnakeGame(mazeSize, head0, tailDirection0, head1, tailDirection1, snakeSize, bot0, bot1);
                    game.runWithoutPauses(STEPS_PER_GAME);
                    tournamentResults[i] += Integer.parseInt(game.gameResult.substring(0, 1));
                    tournamentResults[j] += Integer.parseInt(game.gameResult.substring(game.gameResult.length() - 1));
                }
        }

        ArrayList<Pair<Node, Integer>> results = new ArrayList<>();
        for (int i = 0; i < trees.size(); i++)
            results.add(new Pair<>(trees.get(i), tournamentResults[i]));

        return results;
    }

    /**
     * Cross two trees:
     * Choose random subtree in the first tree and exchange it with a random subtree in the second tree
     * Return randomly chosen one of the result trees
     * @param a first parent
     * @param b second parent
     * @return child's tree root node
     */
    private Node crossover(Node a, Node b) {
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

        if (rn.nextInt(1) == 0) // return randomly chosen result
            return a;
        else
            return b;
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
