package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class ManhattanDistanceSnakeBody extends ManhattanDistance {
    /**
     * Given game state and direction calculates the distance to object from given direction
     *
     * @param direction direction to calculate goodness for
     * @param snake     information about current snake
     * @param opponent  information about opponent's snake
     * @param mazeSize  boardSize
     * @param apple     apple's coordinate
     * @return distance to object
     */
    @Override
    public float value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        int totalDisatance = 0;
        Coordinate head = snake.getHead();
        for (Coordinate coordinate : snake.body) {
            totalDisatance += calculateManhattanDistance(head, coordinate);
        }

        return totalDisatance;
    }
}
