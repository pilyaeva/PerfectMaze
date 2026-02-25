package domain.structures;

import domain.util.ArgumentCheckerUtil;

/**
 * Структурка данных для работы со множествами. То есть для определения того, соединены ли
 * между собой ячейки в лабиринте. Алгоритм хрупкий очень, и работает только с алгоритмом Эллера в
 * паре
 */
public class SetUnion { // SetUnion - объединение множеств
  private final int size;
  private final int[] setUnion;
  private int biggestSetNumber;

  public SetUnion(int size) {
    ArgumentCheckerUtil.checkMazeSize(size);
    this.size = size;
    setUnion = new int[size];
    for (int i = 0; i < size; i++) {
      setUnion[i] = i;
    }
    biggestSetNumber = size - 1;
  }

  public int getId(int index) {
    return setUnion[index];
  }

  /**
   * Проверка того, соединены ли между собой два представителя множеств
   * @param first первый представитель множества
   * @param second второй представитель множества
   * @return true, если это представители одного множества
   */
  public boolean areConnected(int first, int second) {
    ArgumentCheckerUtil.checkIndexBounds(first, size);
    ArgumentCheckerUtil.checkIndexBounds(second, size);

    return setUnion[first] == setUnion[second];
  }

  /**
   * Объединение двух представителей множеств
   * @param first первый представитель
   * @param second второй представитель
   * @implNote <br><br>Второй элемент должен находиться сразу после первого
   */
  public void union(int first, int second) {
    ArgumentCheckerUtil.checkIndexBounds(first, size);
    ArgumentCheckerUtil.checkIndexBounds(second, size);
    int oldId = setUnion[second];
    int newId = setUnion[first];

    // Меняем ID для ВСЕХ ячеек, которые принадлежали к старому множеству
    for (int i = 0; i < size; i++) {
      if (setUnion[i] == oldId) {
        setUnion[i] = newId;
      }
    }
  }

  public void disunion(int value) {
    ArgumentCheckerUtil.checkIndexBounds(value, size);
    //    biggestSetNumber += 1;
    setUnion[value] = ++biggestSetNumber;
  }

  /**
   * Метод для определения, является ли <code>value</code> единственным в множестве
   * @param value представитель множества
   * @return <code>true</code>, если множество состоит из одного элемента
   */
  public boolean isAlone(int value) {
    ArgumentCheckerUtil.checkIndexBounds(value, size);
    if (value == 0) {
      return setUnion[value] != setUnion[value + 1];
    } else if (value == size - 1) {
      return setUnion[value] != setUnion[value - 1];
    } else {
      return setUnion[value] != setUnion[value - 1] && setUnion[value] != setUnion[value + 1];
    }
  }

  /**
   * Проверка на то, что текущий представитель множества (<code>value</code>) является последним
   * представителем в своем множестве
   * @param value представитель множества
   * @return <code>true</code> если является последним представителем своего множества
   */
  public boolean isLastInSet(int value) {
    ArgumentCheckerUtil.checkIndexBounds(value, size);
    if (value == size - 1) {
      return true;
    }

    return setUnion[value] != setUnion[value + 1];
  }

  public void print() {
    for (int i = 0; i < size; i++) {
      System.out.printf("%d ", setUnion[i]);
    }
    System.out.println();
  }
}
