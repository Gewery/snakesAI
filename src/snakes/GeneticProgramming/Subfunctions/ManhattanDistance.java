package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

/**
 *
 */
abstract class ManhattanDistance implements Subfunction {
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
    abstract public float value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple);

    /**
     * Calculate Manhattan Distance between two coordinates
     *
     * @param a start coordinate
     * @param b finish coordinate
     * @return distance
     */
    protected float calculateManhattanDistance(Coordinate a, Coordinate b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}
