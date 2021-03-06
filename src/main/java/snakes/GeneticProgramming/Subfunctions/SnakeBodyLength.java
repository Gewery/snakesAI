package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class SnakeBodyLength implements Subfunction {
    /**
     * Given game state and direction return snake's body length
     *
     * @param direction direction to calculate goodness for
     * @param snake     information about current snake
     * @param opponent  information about opponent's snake
     * @param mazeSize  boardSize
     * @param apple     apple's coordinate
     * @return any float value in range [-1; 1], the larger the number, the better for the snake this direction
     * -1 - bad direction
     * 0 - neutral direction
     * 1 - perfect direction
     */
    @Override
    public double value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        return (double) snake.body.size() / (mazeSize.x * mazeSize.y);
    }
}
