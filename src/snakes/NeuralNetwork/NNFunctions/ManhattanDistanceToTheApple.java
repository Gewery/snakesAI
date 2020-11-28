package snakes.NeuralNetwork.NNFunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class ManhattanDistanceToTheApple implements NNFunction {
    @Override
    public double value(
        Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple
    ) {
        Coordinate start = snake.getHead();
        Coordinate current = start.moveTo(direction);
        int distance = Math.abs(current.x - apple.x) + Math.abs(current.y - apple.y);


        return distance * 1.0 / Math.max(mazeSize.x, mazeSize.y);
    }
}
