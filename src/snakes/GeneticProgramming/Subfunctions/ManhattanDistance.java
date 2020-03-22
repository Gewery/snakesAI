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
    abstract public double value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple);

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

    /**
     * Calculate maximum possible distance between 2 object on the maze
     *
     * @param mazeSize maze size
     * @return maximum possible distance between 2 object
     */
    protected float calculateDiagonalDistance(Coordinate mazeSize) {
        Coordinate zeroCoordinate = new Coordinate(0, 0);
        return calculateManhattanDistance(zeroCoordinate, mazeSize);
    }

    /**
     * Calculate maximum distance from starting point (0, 0) to each cell of maze
     *
     * @return maximum distance from starting point (0, 0) to each cell of maze
     */
    protected float calculateMaxBodyDistance(Coordinate mazeSize) {
        int totalDistance = 0;

        for (int i = 1; i <= mazeSize.y; i++) {
            for (int j = 0; j < mazeSize.x; j++) {
                totalDistance += (i - 1 + j);
            }
        }

        return totalDistance;
    }
}
