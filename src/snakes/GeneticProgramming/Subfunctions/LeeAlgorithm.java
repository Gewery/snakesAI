package snakes.GeneticProgramming.Subfunctions;

import snakes.Coordinate;
import snakes.Direction;

import java.util.LinkedList;
import java.util.Queue;

class LeeAlgorithm {
    int ROW, COL;

    // Row and column numbers of 4 neighbours of a given cell
    Direction[] moves = {Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT};

    public LeeAlgorithm(Coordinate mazeSize) {
        this.ROW = mazeSize.x;
        this.COL = mazeSize.y;
    }

    // check whether given cell (row, col) fit the board size.
    boolean isValid(int row, int col) {
        // return true if row number and column number is in range
        return (row >= 0) && (row < this.ROW) && (col >= 0) && (col < COL);
    }

    // function to find the shortest path between a given source cell to a destination cell.
    int calculateShortestPath(int[][] board, Coordinate src, Coordinate dest) {

        // check source and destination cell of the matrix have value 1
        if (board[src.x][src.y] != 1 || board[dest.x][dest.y] != 1)
            return -2;

        boolean[][] visited = new boolean[ROW][COL];

        // Mark the source cell as visited
        visited[src.x][src.y] = true;

        // Create a queue for BFS
        Queue<Node> queue = new LinkedList<>();

        // Distance of source cell is 0
        Node sourceNode = new Node(src, 0);
        queue.add(sourceNode); // Enqueue source cell

        // Do a BFS starting from source cell
        while (!queue.isEmpty()) {
            Node current = queue.peek();
            Coordinate pt = current.coordinate;

            // If we have reached the destination cell, we are done
            if (pt.x == dest.x && pt.y == dest.y)
                return current.distance;

            // Otherwise dequeue the front cell in the queue and enqueue its adjacent cells
            queue.remove();


            for (Direction move : moves) {
                int row = pt.x + move.dx;
                int col = pt.y + move.dy;

                // if adjacent cell is valid, has path and not visited yet, enqueue it.
                if (isValid(row, col) && board[row][col] == 1 && !visited[row][col]) {
                    // mark cell as visited and enqueue it
                    visited[row][col] = true;
                    Node adjCell = new Node(new Coordinate(row, col), current.distance + 1);
                    queue.add(adjCell);
                }
            }
        }

        // Return -1 if destination cannot be reached
        return -1;
    }

    // A Data Structure for queue used in BFS
    static class Node {
        Coordinate coordinate; // The coordinates of a cell
        int distance; // cell's distance of from the source

        public Node(Coordinate coordinate, int distance) {
            this.coordinate = coordinate;
            this.distance = distance;
        }
    }
}
