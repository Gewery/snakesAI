package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class DensityChange implements Subfunction {

    /**
     * Given game state and direction calculates the change of density of this direction
     *
     * @param direction direction to calculate goodness for
     * @param snake     information about current snake
     * @param opponent  information about opponent's snake
     * @param mazeSize  boardSize
     * @param apple     apple's coordinate
     * @return any float value, the larger the number, the better for the snake this direction
     */
    @Override
    public float value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        return 0;
    }
}
