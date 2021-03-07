package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class ManhattanDistanceOpponentHead extends ManhattanDistance {
    /**
     * Given game state and direction calculates the distance to opponent head from given direction of snake
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
        Coordinate head = snake.getHead();
        Coordinate headOpponent = opponent.getHead();
        return calculateManhattanDistance(head.moveTo(direction), headOpponent) / calculateDiagonalDistance(mazeSize);
    }
}
