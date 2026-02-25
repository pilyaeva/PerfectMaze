package domain.model;

import domain.util.ArgumentCheckerUtil;

/**
 * Представитель идеального лабиринта (без зацикленностей и тупиков)<br>
 * Внутри класса лабиринт представлен в виде двух массивов. Из массива, содержащего данные о правых
 * стенах ячейки, и массива, содержащего данные о левых ячейках<br><br>
 *
 * Заполнение массива с данными о правых стенах:<br>
 * <code>0</code> - нет правой границы ( )<br>
 * <code>1</code> - есть правая граница ( |)<br><br>
 *
 * Заполнение массива с данными о нижних стенках:<br>
 * <code>0</code> - нет нижней границы ( )<br>
 * <code>1</code> - есть нижняя граница (_)<br>
 */
public class PerfectMaze {
  private final int rows; // количество строк в лабиринте
  private final int columns; // количество столбцов в лабиринте
  private final int[][] rightWallsMatrix;
  private final int[][] bottomWallsMatrix;

  public PerfectMaze(int rows, int columns, int[][] rightWallsMatrix, int[][] bottomWallsMatrix) {
    ArgumentCheckerUtil.checkMazeSize(rows);
    ArgumentCheckerUtil.checkMazeSize(columns);
    this.rows = rows;
    this.columns = columns;

    this.rightWallsMatrix = rightWallsMatrix;
    this.bottomWallsMatrix = bottomWallsMatrix;
  }

  public int[][] getRightWallsMatrix() {
    return rightWallsMatrix;
  }

  public int[][] getBottomWallsMatrix() {
    return bottomWallsMatrix;
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }
}
