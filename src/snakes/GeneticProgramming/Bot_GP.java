package snakes.GeneticProgramming;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.GeneticProgramming.Subfunctions.DistanceToWall;
import snakes.Snake;

public class Bot_GP implements Bot {
    private Node root;

    public Bot_GP(Node root) {
        this.root = root;
    }

    private boolean isValid(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize) {
        Coordinate newHead = snake.getHead().moveTo(direction);
        if (!newHead.inBounds(mazeSize))
            return false; // Left maze
        if (snake.elements.contains(newHead))
            return false; // Collided with itself
        if (opponent.elements.contains(newHead))
            return false; // Collided with opponent

        return true;
    }

    @Override
    public Direction chooseDirection(Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        Direction best_direction = null;
        double max_score = -Float.MAX_VALUE;

        for (Direction to : Direction.values()) {
            if (isValid(to, snake, opponent, mazeSize)) {
                double cur = root.computeValue(to, snake, opponent, mazeSize, apple);
                if (cur >= max_score) {
                    max_score = cur;
                    best_direction = to;
                }
            }
        }
        if (best_direction == null) { // Function is incorrect OR no valid directions
            return Direction.LEFT;
        }

        return best_direction;
    }
}
