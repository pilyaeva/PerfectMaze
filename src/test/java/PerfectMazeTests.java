import static org.junit.jupiter.api.Assertions.*;

import domain.algorithm.PerfectMazeGenerator;
import domain.constants.Constants;
import domain.model.PerfectMaze;
import java.awt.*;
import java.util.Stack;
import org.junit.jupiter.api.Test;

public class PerfectMazeTests {
  @Test
  void isPerfect() {
    for (int i = 0; i < 1000; i++) {
      PerfectMazeGenerator mazeGenerator = new PerfectMazeGenerator(50, 50);
      PerfectMaze maze = mazeGenerator.create();
      assertTrue(testConnectivity(maze));
      assertTrue(testNoCycles(maze));
    }
  }

  @Test
  void boundsThrows() {
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> new PerfectMazeGenerator(0, 0));
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> new PerfectMazeGenerator(1, 1));
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> new PerfectMazeGenerator(-1, -1));
  }

  private boolean testConnectivity(PerfectMaze maze) {
    int rows = maze.getRows(), columns = maze.getColumns();
    int[][] rightWallsMatrix = maze.getRightWallsMatrix();
    int[][] bottomWallsMatrix = maze.getBottomWallsMatrix();

    boolean[][] visited = new boolean[rows][columns];
    int visitedCount = 0;

    Stack<Point> stack = new Stack<>();
    stack.push(new Point(0, 0));

    while (!stack.isEmpty()) {
      Point current = stack.pop();
      if (visited[current.x][current.y])
        continue;

      visited[current.x][current.y] = true;
      visitedCount++;

      // Добавляем соседей, до которых можно дойти (нет стен)
      if (current.x > 0 && bottomWallsMatrix[current.x - 1][current.y] == Constants.EMPTY) {
        stack.push(new Point(current.x - 1, current.y));
      }
      if (current.x < rows - 1 && bottomWallsMatrix[current.x][current.y] == Constants.EMPTY) {
        stack.push(new Point(current.x + 1, current.y));
      }
      if (current.y > 0 && rightWallsMatrix[current.x][current.y - 1] == Constants.EMPTY) {
        stack.push(new Point(current.x, current.y - 1));
      }
      if (current.y < columns - 1 && rightWallsMatrix[current.x][current.y] == Constants.EMPTY) {
        stack.push(new Point(current.x, current.y + 1));
      }
    }

    // Все ячейки должны быть посещены
    return visitedCount == rows * columns;
  }

  public boolean testNoCycles(PerfectMaze maze) {
    int rows = maze.getRows(), columns = maze.getColumns();
    int[][] rightWallsMatrix = maze.getRightWallsMatrix();
    int[][] bottomWallsMatrix = maze.getBottomWallsMatrix();
    int passageCount = 0;

    // Считаем правые проходы
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns - 1; j++) {
        if (rightWallsMatrix[i][j] == 0) {
          passageCount++;
        }
      }
    }

    // Считаем нижние проходы
    for (int i = 0; i < rows - 1; i++) {
      for (int j = 0; j < columns; j++) {
        if (bottomWallsMatrix[i][j] == 0) {
          passageCount++;
        }
      }
    }

    // Должно быть ровно (rows * columns - 1) проходов
    return passageCount == (rows * columns - 1);
  }
}
