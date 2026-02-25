package domain.algorithm;

import domain.constants.Constants;
import domain.model.PerfectMaze;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Реализация алгоритма поиска в глубину
 */
public class PerfectMazeDFS {
  private final int[][] rightWallsMatrix;
  private final int[][] bottomWallsMatrix;
  private final int rows;
  private final int columns;

  public PerfectMazeDFS(PerfectMaze maze) {
    rightWallsMatrix = maze.getRightWallsMatrix();
    bottomWallsMatrix = maze.getBottomWallsMatrix();
    rows = maze.getRows();
    columns = maze.getColumns();
  }

  /**
   * Поиск пути от <code>start</code> до <code>end</code>.
   * @param start координата начала пути (<code>Point(x, y)</code>)
   * @param end координата конца пути (<code>Point(x, y)</code>)
   * @return (<code>List< Point ></code>), если путь найден. Если не найден - null. При успехе
   *     возвращается
   * весь путь в координатах от <code>start</code> до <code>end</code>
   */
  public List<Point> findPath(Point start, Point end) {
    boolean[][] visited = new boolean[rows][columns];
    // Хэш-таблица для восстановления пути. Key - текущая точка, Value - родитель
    Map<Point, Point> parents = new HashMap<>();
    // Стек для хранения всех точек для обхода
    Stack<Point> stack = new Stack<>();

    stack.push(start);

    while (!stack.isEmpty()) {
      Point current = stack.pop();

      // уже посещена, пофиг, пропускаем
      if (visited[current.y][current.x])
        continue;

      // отмечаем как посещенную
      visited[current.y][current.x] = true;

      if (current.equals(end)) {
        return getPath(start, end, parents);
      }

      List<Point> neighbours = getNeighbours(current);
      for (Point neighbour : neighbours) {
        // если не посетили, нужно добавить в очередь для посещения
        if (!visited[neighbour.y][neighbour.x]) {
          stack.push(neighbour);
          // отмечаем соседние как ключи, а текущую ячейку - как родитель соседей
          parents.put(neighbour, current);
        }
      }
    }

    // путь не был найден, а это плохо и печально, дамы и господа!
    return null;
  }

  private List<Point> getNeighbours(Point current) {
    int column = current.x, row = current.y;
    List<Point> neighbours = new ArrayList<>();

    // верх
    if (row > 0 && bottomWallsMatrix[row - 1][column] == Constants.EMPTY)
      neighbours.add(new Point(column, row - 1));

    // право
    if (column < columns - 1 && rightWallsMatrix[row][column] == Constants.EMPTY)
      neighbours.add(new Point(column + 1, row));

    // низ
    if (row < rows - 1 && bottomWallsMatrix[row][column] == Constants.EMPTY)
      neighbours.add(new Point(column, row + 1));

    // лево
    if (column > 0 && rightWallsMatrix[row][column - 1] == Constants.EMPTY)
      neighbours.add(new Point(column - 1, row));

    return neighbours;
  }

  private List<Point> getPath(Point start, Point end, Map<Point, Point> parents) {
    Point current = end;
    List<Point> path = new ArrayList<>();

    while (!current.equals(start)) {
      path.add(current);
      current = parents.get(current);
    }

    path.add(start);
    Collections.reverse(path);

    return path;
  }
}
