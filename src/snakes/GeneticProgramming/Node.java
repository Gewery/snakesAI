package snakes.GeneticProgramming;


import snakes.Coordinate;
import snakes.Direction;
import snakes.GeneticProgramming.Operations.Operation;
import snakes.GeneticProgramming.Operations.OperationsLoader;
import snakes.GeneticProgramming.Subfunctions.Subfunction;
import snakes.GeneticProgramming.Subfunctions.SubfunctionsLoader;
import snakes.Snake;

import java.util.ArrayList;
import java.util.Random;

public class Node {
    /**
     * type - defines what is stored in this node
     * 0 - constant value
     * 1 - subfunction
     * 2 - operaton (+/-/...)
     */
    int type;
    float constant_value;
    Subfunction subfunction;
    Operation operation;
    Node left = null, right = null, parent = null;
    Random rn = new Random();
    static ArrayList<Subfunction> availableSubfunctions = new SubfunctionsLoader().getAllSubfunctions();
    static ArrayList<Operation> availableOperations = new OperationsLoader().getAllOperations();

    // fix chosing parents
    // crossover with probability
    // low probability of mutation

    /**
     * initialization with random numbers/subfunctions/operation
     * @param type type of node
     * @param parent parent node (null if this is a root)
     */
    public Node(int type, Node parent) {
        this.type = type;
        this.parent = parent;
        NodeInit();
    }

    /**
     * Type of node will be chosen randomly
     * @param parent parent node (null if this is a root)
     */
    public Node(Node parent) {
        type = rn.nextInt(3);
        this.parent = parent;
        NodeInit();
    }

    /**
     * Create and return a copy of the Node
     * @param t Node to copy
     * @param leftChild copied left child
     * @param rightChild copied right child
     */
    public Node(Node t, Node leftChild, Node rightChild) {
        this.type = t.type;
        this.constant_value = t.constant_value;
        this.subfunction = t.subfunction;
        this.operation = t.operation;
        this.left = leftChild;
        this.right = rightChild;
        if (leftChild != null)
            leftChild.parent = this;
        if (rightChild != null)
            rightChild.parent = this;
    }

    private void NodeInit() {
        switch (this.type) {
            case 0: constant_value = rn.nextFloat(); break;
            case 1: subfunction = availableSubfunctions.get(rn.nextInt(availableSubfunctions.size())); break;
            case 2: operation = availableOperations.get(rn.nextInt(availableOperations.size())); break;
        }
    }

    public float computeValue(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        switch (type) {
            case 0: return constant_value;
            case 1: return subfunction.value(direction, snake, opponent, mazeSize, apple);
            case 2: return operation.calculate(left.computeValue(direction, snake, opponent, mazeSize, apple), right.computeValue(direction, snake, opponent, mazeSize, apple));
        }

        return 0;
    }

    /**
     * generates a subtree assuming that this node is a current root
     * @param max_height max height of desired subtree
     */
    public void generateSubtree(int max_height) {
        if (this.type != 2 || max_height == 0)
            return;

        if (max_height == 1) { // must create leaf children
            this.left = new Node(rn.nextInt(2), this);
            this.right = new Node(rn.nextInt(2), this);
        }
        else {
            this.left = new Node(this);
            this.right = new Node(this);
        }
        this.left.parent = this;
        this.right.parent = this;

        this.right.generateSubtree(max_height - 1);
        this.left.generateSubtree(max_height - 1);
    }

    /**
     * Determine if current node is a leaf of the tree
     * @return true if current node is a leaf(constant of subfunction)
     */
    public boolean isLeaf() {
        return this.type != 2;
    }

    /**
     * Change one of the child to new one
     * @param oldChild child to be replaced
     * @param newChild new child that will replace old one
     */
    public void changeChild(Node oldChild, Node newChild) {
        if (this.left == oldChild)
            this.left = newChild;
        else if (this.right == oldChild)
            this.right = newChild;
        else { //TODO remove it
            // Error: oldChild is not the child of this!
            System.out.println("Wrong call of changeChild()");
        }
    }
}
