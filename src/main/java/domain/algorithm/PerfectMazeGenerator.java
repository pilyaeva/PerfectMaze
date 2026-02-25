package domain.algorithm;

import domain.constants.Constants;
import domain.model.PerfectMaze;
import domain.structures.SetUnion;
import domain.util.ArgumentCheckerUtil;
import java.util.Random;

public class PerfectMazeGenerator {
  private final int rows;
  private final int columns;
  private final Random random;
  private final SetUnion setUnion;

  private final int[][] rightWallsMatrix;
  private final int[][] bottomWallsMatrix;

  public PerfectMazeGenerator(int rows, int columns) {
    ArgumentCheckerUtil.checkMazeSize(rows);
    ArgumentCheckerUtil.checkMazeSize(columns);
    this.rows = rows;
    this.columns = columns;

    random = new Random();
    setUnion = new SetUnion(columns);

    rightWallsMatrix = new int[rows][columns];
    bottomWallsMatrix = new int[rows][columns];
  }

  /**
   * Метод для создания идеального лабиринта исходя из размеров лабиринта, полученных в
   * конструкторе<br><br>
   *
   * Массив mazeRow имеет следующую структуру: <br>
   * <code>0</code> - ни правой ни нижней стены<br>
   * <code>1</code> - только правая стена<br>
   * <code>2</code> - только нижняя стена<br>
   * <code>3</code> - и правая и нижняя стена
   */
  public PerfectMaze create() {
    int[] mazeRow = new int[columns];

    for (int row = 0; row < rows; row++) {
      randomRightWalls(mazeRow);
      randomBottomWalls(mazeRow);
      if (row != rows - 1) {
        mazeRowToFields(row, mazeRow);
        clearMazeRow(mazeRow);
      } else {
        processLastRow(mazeRow);
        mazeRowToFields(row, mazeRow);
      }
    }

    return new PerfectMaze(rows, columns, rightWallsMatrix, bottomWallsMatrix);
  }

  /**
   * То же самое, что и метод create(), только в процессе создания лабиринта он отображается в
   * терминале
   */
  public PerfectMaze createAndShow() {
    int[] mazeRow = new int[columns];

    for (int row = 0; row < rows; row++) {
      randomRightWalls(mazeRow);
      randomBottomWalls(mazeRow);
      if (row != rows - 1) {
        mazeRowToFields(row, mazeRow);
        printMazeRow(mazeRow);
        clearMazeRow(mazeRow);
      } else {
        processLastRow(mazeRow);
        mazeRowToFields(row, mazeRow);
        printMazeRow(mazeRow);
      }
    }

    return new PerfectMaze(rows, columns, rightWallsMatrix, bottomWallsMatrix);
  }

  private void randomRightWalls(int[] mazeRow) {
    for (int i = 0; i < columns - 1; i++) {
      if (setUnion.areConnected(i, i + 1)) { // если в едином множестве, то ставим правую стенку
        mazeRow[i] = Constants.RIGHT_WALL;
      } else { // если не в одном множестве
        int value = randomZeroOrOne();

        if (value == Constants.RIGHT_WALL) { // просто ставим стенку
          mazeRow[i] = Constants.RIGHT_WALL;
        } else { // стенку не ставим, объединяем множества в одно
          setUnion.union(i, i + 1);
        }
      }
    }
    mazeRow[columns - 1] = Constants.RIGHT_WALL;
  }

  private void randomBottomWalls(int[] bottomBoards) {
    int emptyCount = 0;

    for (int i = 0; i < columns; i++) {
      // Ячейка лабиринта не одна в своем множестве.
      // А если одна, то мы нижнюю границу не ставим (то есть пропускаем итерацию)

      if (!setUnion.isAlone(
              i)) { // если множество состоит из одного элемента, то пропускаем итерацию
        int value = randomZeroOrTwo();
        if (value == Constants.EMPTY)
          ++emptyCount;

        boolean isLast = setUnion.isLastInSet(i);

        // гарантия того, что хотя бы одна ячейка в множестве будет
        // без нижней стены (в данном случае это уже последняя)
        if (isLast && emptyCount == 0) {
          continue;
        }

        // сумма, т.к. мы учитываем, стоит ли еще правая стенка тут, если да, то у нас уже
        // число 3 в ячейке, а если нет, то число 2
        bottomBoards[i] += value;
        if (isLast)
          emptyCount = 0;
      }
    }
  }

  // для генерации правых стенок (Constants.RIGHT_WALL)
  private int randomZeroOrOne() {
    return random.nextInt(0, 2);
  }
  // для генерации нижних стенок (Constants.BOTTOM_WALL)
  private int randomZeroOrTwo() {
    int val = randomZeroOrOne();
    return val * 2;
  }

  private void mazeRowToFields(int rowNum, int[] mazeRow) {
    for (int i = 0; i < columns; i++) {
      switch (mazeRow[i]) {
        case Constants.RIGHT_WALL ->
                rightWallsMatrix[rowNum][i] = 1;
        case Constants.BOTTOM_WALL ->
                bottomWallsMatrix[rowNum][i] = 1;
        case Constants.RIGHT_BOTTOM_WALLS -> {
          rightWallsMatrix[rowNum][i] = 1;
          bottomWallsMatrix[rowNum][i] = 1;
        }
      }
    }
  }

  private void clearMazeRow(int[] mazeRow) {
    for (int i = 0; i < columns; i++) {

      boolean hasBottomWall = mazeRow[i] == Constants.BOTTOM_WALL || mazeRow[i] == Constants.RIGHT_BOTTOM_WALLS;
      mazeRow[i] = Constants.EMPTY;

      if (hasBottomWall) {
        setUnion.disunion(i); // если нижняя граница, то нужно элемент удалить из его множества
      }
    }
  }

  private void processLastRow(int[] mazeRow) {
    for (int i = 0; i < columns - 1; i++) {
      // Добавляем нижнюю стену всем
      switch (mazeRow[i]) {
        case Constants.EMPTY ->
                mazeRow[i] = Constants.BOTTOM_WALL;
        case Constants.RIGHT_WALL ->
                mazeRow[i] = Constants.RIGHT_BOTTOM_WALLS;
      }

      // Если ячейки в разных множествах - убираем правую стену
      if (!setUnion.areConnected(i, i + 1)) {
        // Убираем правую стену, но оставляем нижнюю
        if (mazeRow[i] == Constants.RIGHT_BOTTOM_WALLS) {
          mazeRow[i] = Constants.BOTTOM_WALL;
        }
        // Если была только нижняя стена, она и остается
        setUnion.union(i, i + 1);
      }
    }
    mazeRow[columns - 1] = Constants.RIGHT_BOTTOM_WALLS;
  }



  private void printMazeRow(int[] mazeRow) {
    for (int i = 0; i < columns; i++) {
      if (i == 0) System.out.print("|");
      switch (mazeRow[i]) {
        case Constants.EMPTY ->
                System.out.print("  ");
        case Constants.RIGHT_WALL ->
                System.out.print(" |");
        case Constants.BOTTOM_WALL ->
                System.out.print("__");
        case Constants.RIGHT_BOTTOM_WALLS ->
                System.out.print("_|");
      }

    }
    System.out.print("\n");
  }
}
