package domain.util;

import domain.model.PerfectMaze;
import java.io.*;

public class Converter {
  public static void saveMazeToFile(PerfectMaze maze, String filename) {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
      int[][] rightWalls = maze.getRightWallsMatrix();
      int[][] bottomWalls = maze.getBottomWallsMatrix();
      int rows = maze.getRows(), columns = maze.getColumns();

      bw.write(rows + " " + columns);
      bw.newLine();
      matrixToFile(bw, rightWalls, rows, columns);
      bw.newLine();
      matrixToFile(bw, bottomWalls, rows, columns);
    } catch (Exception e) {
      throw new IllegalArgumentException("Something went wrong: " + e.getMessage());
    }
  }

  private static void matrixToFile(BufferedWriter bw, int[][] matrix, int rows, int columns)
      throws IOException {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        bw.write(String.valueOf(matrix[i][j]));
        if (j < columns - 1)
          bw.write(" ");
      }
      bw.newLine();
    }
  }

  public static PerfectMaze loadMazeFromFile(File file) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(file));
    String sizeLine = br.readLine().trim();
    String[] mazeSizes = sizeLine.split("\\s+");
    if (mazeSizes.length != 2)
      throw new IOException("Wrong size format");

    int rows = Integer.parseInt(mazeSizes[0]);
    int cols = Integer.parseInt(mazeSizes[1]);

    int[][] rightWalls = readMatrix(br, rows, cols);
    br.readLine();
    int[][] bottomWalls = readMatrix(br, rows, cols);

    return new PerfectMaze(rows, cols, rightWalls, bottomWalls);
  }

  private static int[][] readMatrix(BufferedReader br, int rows, int cols) throws IOException {
    int[][] matrix = new int[rows][cols];

    for (int i = 0; i < rows; i++) {
      String line = br.readLine();
      if (line == null)
        throw new IOException("Not enough rows in the matrix");

      String[] numbers = line.trim().split("\\s+");
      if (numbers.length != cols)
        throw new IOException("Incorrect number of columns in a row");

      for (int j = 0; j < cols; j++) {
        matrix[i][j] = Integer.parseInt(numbers[j]);
      }
    }
    return matrix;
  }
}
