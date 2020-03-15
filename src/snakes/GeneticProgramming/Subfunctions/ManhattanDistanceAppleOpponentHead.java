package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class ManhattanDistanceAppleOpponentHead extends ManhattanDistance {
    /**
     * Given game state and direction calculates the distance to apple from given opponent coordinate
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
        Coordinate headOpponent = opponent.getHead();
        return calculateManhattanDistance(headOpponent, apple) / calculateDiagonalDistance(mazeSize);
    }
}
