package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

abstract class ShortestPath implements Subfunction {
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

    protected float calculateShortestPath(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize) {
        return 0;
    }

    /**
     * Queue node used in BFS
     */
    class Node {
        // (x, y) represents matrix cell coordinates
        // dist represent its minimum distance from the source
        int x, y, dist;

        Node(int x, int y, int dist) {
            this.x = x;
            this.y = y;
            this.dist = dist;
        }
    }
}
