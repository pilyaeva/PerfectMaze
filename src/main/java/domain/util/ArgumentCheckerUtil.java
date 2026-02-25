package domain.util;

import domain.constants.Constants;

public class ArgumentCheckerUtil {
  public static void checkMazeSize(int value) {
    if (value <= 1 || value > Constants.MAX_SIZE_MAZE) {
      throw new ArrayIndexOutOfBoundsException(
          "size cannot be one or less and more " + Constants.MAX_SIZE_MAZE + ", size: " + value);
    }
  }

  public static void checkIndexBounds(int value, int maxValue) {
    if (value < 0 || value >= maxValue)
      throw new ArrayIndexOutOfBoundsException("row value is out of range, value: " + value);
  }
}
