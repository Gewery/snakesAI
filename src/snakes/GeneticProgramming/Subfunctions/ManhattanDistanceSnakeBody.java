package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class ManhattanDistanceSnakeBody extends ManhattanDistance {
    /**
     * Given game state and direction calculates the sum of the distances from snake's head to each piece of its body
     *
     * @param direction direction to calculate goodness for
     * @param snake     information about current snake
     * @param opponent  information about opponent's snake
     * @param mazeSize  boardSize
     * @param apple     apple's coordinate
     * @return distance to object
     */
    @Override
    public double value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        int totalDistance = 0;


        Coordinate head = snake.getHead();
        for (Coordinate coordinate : snake.body) {
            totalDistance += calculateManhattanDistance(head, coordinate);
        }

        return totalDistance / calculateMaxBodyDistance(mazeSize);
    }
}
