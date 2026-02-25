import static org.junit.jupiter.api.Assertions.assertNotNull;

import domain.algorithm.PerfectMazeDFS;
import domain.algorithm.PerfectMazeGenerator;
import domain.model.PerfectMaze;
import java.awt.*;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class PathFinderTests {
  @Test
  void pathTest() {
    int target = 10000;
    int size = 50;

    Random random = new Random();

    for (int i = 0; i < target; i++) {
      PerfectMazeGenerator generator = new PerfectMazeGenerator(size, size);
      PerfectMaze maze = generator.create();
      PerfectMazeDFS dfs = new PerfectMazeDFS(maze);
      int startX = randomCoordinate(random, size);
      int startY = randomCoordinate(random, size);
      int endX = randomCoordinate(random, size);
      int endY = randomCoordinate(random, size);
      List<Point> path = dfs.findPath(new Point(startX, startY), new Point(endX, endY));

      assertNotNull(path);
    }
  }

  private int randomCoordinate(Random random, int max) {
    return random.nextInt(0, max);
  }
}
