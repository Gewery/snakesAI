package snakes.GeneticProgramming;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class Bot_GP implements Bot {
    static Node root;

    public Bot_GP(Node root) {
        this.root = root;
    }

    @Override
    public Direction chooseDirection(Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        Direction best_direction = null;
        double max_score = -Float.MAX_VALUE;

        for (Direction to : Direction.values()) {
            double cur = root.computeValue(to, snake, opponent, mazeSize, apple);
            if (cur >= max_score) {
                max_score = cur;
                best_direction = to;
            }
        }
        if (best_direction == null) { // Function is incorrect
            return Direction.LEFT;
        }

        return best_direction;
    }

    public static String TreeTraverse() {
        return printTree(root);
    }

    private static String printTree(Node t) {
        switch (t.type) {
            case 0: return "\nconstant_value: " + t.constant_value;
            case 1: return "\nsubfunction: " + t.subfunction.getClass();
            case 2: return "\noperation: " + t.operation.getClass() + printTree(t.left) + printTree(t.right);
        }
        return "";
    }
}
