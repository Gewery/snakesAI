package snakes.GeneticProgramming;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class Bot_GP implements Bot {
    Node root;

    public Bot_GP(Node root) {
        this.root = root;
    }

    @Override
    public Direction chooseDirection(Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        Direction best_direction = null;
        float max_score = -Float.MAX_VALUE;

        for (Direction to : Direction.values()) {
            float cur = root.computeValue(to, snake, opponent, mazeSize, apple);
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
}
