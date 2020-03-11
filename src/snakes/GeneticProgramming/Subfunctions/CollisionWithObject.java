package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

public class CollisionWithObject implements Subfunction {
    @Override
    public float value(Direction direction, Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        Coordinate to = snake.getHead().moveTo(direction);
        return (snake.body.contains(to) || opponent.body.contains(to) || !to.inBounds(mazeSize)) ? -1 : 0;
    }
}
