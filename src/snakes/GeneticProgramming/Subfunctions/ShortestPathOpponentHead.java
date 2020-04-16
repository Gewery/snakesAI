package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class ShortestPathOpponentHead extends ShortestPath {
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
    public double value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        Coordinate head = snake.getHead();
        Coordinate source = head.moveTo(direction);

        Coordinate headOpponent = opponent.getHead();

        Snake movedSnake = snake.clone();
        movedSnake.moveTo(direction, false);
        return calculateShortestPath(source, headOpponent, movedSnake, opponent, mazeSize, apple) / calculateDiagonalDistance(mazeSize);
    }
}
