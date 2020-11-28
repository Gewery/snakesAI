package snakes.NeuralNetwork.NNFunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class DistanceToAnyObject implements NNFunction {
    @Override
    public double value(
        Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple
    ) {
        Coordinate start = snake.getHead();
        int distance = 0;
        Coordinate current = start.moveTo(direction);
        while (current.inBounds(mazeSize)
            && !snake.elements.contains(current)
            && !opponent.elements.contains(current)) {
            current = current.moveTo(direction);
            distance++;
        }

        return distance * 1.0 / Math.max(mazeSize.x, mazeSize.y);
    }
}
