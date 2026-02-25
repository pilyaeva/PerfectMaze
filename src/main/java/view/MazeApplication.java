package view;

import domain.algorithm.PerfectMazeDFS;
import domain.algorithm.PerfectMazeGenerator;
import domain.model.PerfectMaze;
import domain.util.Converter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MazeApplication extends Application {
  private PerfectMaze currentMaze;
  private Canvas mazeArea;

  @Override
  public void start(Stage primaryStage) {
    mazeArea = new Canvas(500, 500);

    Button generateNewMazeBtn = new Button("Generate Maze");
    Button solveMazeBtn = new Button("Solve Maze");
    Button loadMazeBtn = new Button("Load Maze");
    Button saveMazeBtn = new Button("Save Maze");
    generateNewMazeBtn.setMaxWidth(Double.MAX_VALUE);
    loadMazeBtn.setMaxWidth(Double.MAX_VALUE);
    saveMazeBtn.setMaxWidth(Double.MAX_VALUE);

    HBox buttons = new HBox(20, generateNewMazeBtn, solveMazeBtn, loadMazeBtn, saveMazeBtn);
    buttons.setAlignment(Pos.CENTER);

    VBox root = new VBox(10, mazeArea, buttons);
    root.setAlignment(Pos.CENTER);

    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Maze Application");
    primaryStage.setResizable(false);
    primaryStage.setWidth(530);
    primaryStage.setHeight(600);
    primaryStage.show();

    generateNewMazeBtn.setOnAction(e -> handleGenerateNewMaze());
    solveMazeBtn.setOnAction(e -> handleSolveMaze());
    loadMazeBtn.setOnAction(e -> handleLoadMaze());
    saveMazeBtn.setOnAction(e -> handleSaveMaze());
  }

  // Дополнительное окно для ввода размеров нового лабиринта
  public Optional<Pair<Integer, Integer>> showGenerateNewMazeDialog() {
    Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
    dialog.setTitle("Enter Maze sizes");
    ButtonType generateButtonType =
        new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE); // кнопка Generate
    dialog.getDialogPane().getButtonTypes().addAll(
        generateButtonType, ButtonType.CANCEL); // кнопка Cancel

    TextField rowsField = new TextField();
    rowsField.setPromptText("2 - 50");
    TextField colsField = new TextField();
    colsField.setPromptText("2 - 50");

    GridPane grid = new GridPane();
    grid.setHgap(12);
    grid.setVgap(12);
    grid.setPadding(new Insets(20));
    grid.add(new Label("Number of rows:"), 0, 0);
    grid.add(rowsField, 1, 0);
    grid.add(new Label("Number of cols:"), 0, 1);
    grid.add(colsField, 1, 1);
    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogBtn -> {
      if (dialogBtn != generateButtonType)
        return null;
      try {
        int rows = Integer.parseInt(rowsField.getText().trim());
        int cols = Integer.parseInt(colsField.getText().trim());
        return new Pair<>(rows, cols);
      } catch (NumberFormatException e) {
        return null;
      }
    });
    return dialog.showAndWait();
  }

  // Дополнительное окно для ввода координат поиска пути в лабиринте
  public Optional<Pair<Point, Point>> showSolveMazeDialog() {
    Dialog<Pair<Point, Point>> dialog = new Dialog<>();
    dialog.setTitle("Enter start and end coordinates");
    ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE); // кнопка OK
    dialog.getDialogPane().getButtonTypes().addAll(
        okButtonType, ButtonType.CANCEL); // кнопка Cancel

    int rows = currentMaze.getRows();
    int cols = currentMaze.getColumns();

    TextField startRowField = new TextField();
    startRowField.setPromptText("0 - " + (rows - 1));
    TextField startColField = new TextField();
    startColField.setPromptText("0 - " + (cols - 1));
    TextField endRowField = new TextField();
    endRowField.setPromptText("0 - " + (rows - 1));
    TextField endColField = new TextField();
    endColField.setPromptText("0 - " + (cols - 1));

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));
    grid.add(new Label("Start row:"), 0, 0);
    grid.add(startRowField, 1, 0);
    grid.add(new Label("Start col:"), 0, 1);
    grid.add(startColField, 1, 1);
    grid.add(new Label("End row:"), 2, 0);
    grid.add(endRowField, 3, 0);
    grid.add(new Label("End col:"), 2, 1);
    grid.add(endColField, 3, 1);
    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(btn -> {
      if (btn != okButtonType)
        return null;
      try {
        int startY = Integer.parseInt(startRowField.getText().trim());
        int startX = Integer.parseInt(startColField.getText().trim());
        int endY = Integer.parseInt(endRowField.getText().trim());
        int endX = Integer.parseInt(endColField.getText().trim());

        return new Pair<>(new Point(startX, startY), new Point(endX, endY));
      } catch (NumberFormatException e) {
        return null;
      }
    });
    return dialog.showAndWait();
  }

  // Генерация нового лабиринта
  public void handleGenerateNewMaze() {
    Optional<Pair<Integer, Integer>> result = showGenerateNewMazeDialog();
    result.ifPresent(pair -> {
      int rows = pair.getKey();
      int cols = pair.getValue();

      if (rows < 2 || rows > 50 || cols < 2 || cols > 50) {
        showErrorAlert("Incorrect sizes!");
        return;
      }
      PerfectMazeGenerator generator = new PerfectMazeGenerator(rows, cols);
      PerfectMaze newMaze = generator.create();
      currentMaze = newMaze;

      drawMaze(newMaze);
    });
  }

  // Нахождения пути между двумя точками
  private void handleSolveMaze() {
    if (currentMaze == null) {
      showErrorAlert("Generate a maze first!");
      return;
    }
    Optional<Pair<Point, Point>> points = showSolveMazeDialog();
    points.ifPresent(pair -> {
      Point start = pair.getKey();
      Point end = pair.getValue();

      int rows = currentMaze.getRows();
      int cols = currentMaze.getColumns();
      if (!isValidPoint(start, rows, cols) || !isValidPoint(end, rows, cols)) {
        showErrorAlert("Incorrect coordinates!\n "
            + "Range: rows 0–" + (rows - 1) + ", cols 0–" + (cols - 1));
        return;
      }
      PerfectMazeDFS dfs = new PerfectMazeDFS(currentMaze);
      List<Point> path = dfs.findPath(start, end);

      if (path == null || path.isEmpty()) {
        showErrorAlert("Path not found");
      } else {
        drawPath(currentMaze, path);
      }
    });
  }

  // Загрузка лабиринта из файла
  private void handleLoadMaze() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Лабиринт (*.txt)", "*.txt"));
    File selectedFile = fileChooser.showOpenDialog(mazeArea.getScene().getWindow());

    if (selectedFile != null) {
      try {
        currentMaze = Converter.loadMazeFromFile(selectedFile);
        drawMaze(currentMaze);
      } catch (Exception ex) {
        showErrorAlert("Load error\n" + ex.getMessage());
      }
    }
  }

  // Сохранение лабиринта в файл
  private void handleSaveMaze() {
    if (currentMaze == null) {
      showErrorAlert("Create or download maze first!");
      return;
    }
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Лабиринт (*.txt)", "*.txt"));
    fileChooser.setInitialFileName("New_maze");
    File selectedFile = fileChooser.showSaveDialog(mazeArea.getScene().getWindow());

    if (selectedFile != null) {
      String filePath = selectedFile.getAbsolutePath();
      if (!filePath.toLowerCase().endsWith(".txt"))
        filePath += ".txt";

      try {
        Converter.saveMazeToFile(currentMaze, filePath);
      } catch (Exception ex) {
        showErrorAlert("File not saved\n" + ex.getMessage());
      }
    }
  }

  // Отрисовка лабиринта
  private void drawMaze(PerfectMaze maze) {
    GraphicsContext gc = mazeArea.getGraphicsContext2D();
    gc.setFill(Color.WHITE);
    gc.fillRect(0, 0, mazeArea.getWidth(), mazeArea.getHeight());

    int rows = maze.getRows();
    int cols = maze.getColumns();
    double cellSize = Math.min(500.0 / cols, 500.0 / rows); // размер ячейки
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(2); // толщина стен - 2 пикселя

    int[][] rightWalls = maze.getRightWallsMatrix();
    int[][] bottomWalls = maze.getBottomWallsMatrix();
    // отрисовка лабиринта
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        double x = c * cellSize;
        double y = r * cellSize;

        if (rightWalls[r][c] == 1)
          gc.strokeLine(x + cellSize, y, x + cellSize, y + cellSize);

        if (bottomWalls[r][c] == 1)
          gc.strokeLine(x, y + cellSize, x + cellSize, y + cellSize);
      }
    }
    gc.strokeLine(0, 0, cols * cellSize, 0); // Верхняя горизонтальная линия
    gc.strokeLine(0, 0, 0, rows * cellSize); // Левая вертикальная линия
    gc.strokeLine(
        0, rows * cellSize, cols * cellSize, rows * cellSize); // Нижняя горизонтальная линия
    gc.strokeLine(
        cols * cellSize, 0, cols * cellSize, rows * cellSize); // Правая вертикальная линия
  }

  // Отрисовка пути между двумя точками в лабиринте
  private void drawPath(PerfectMaze maze, List<Point> path) {
    drawMaze(maze);
    if (path == null || path.isEmpty())
      return;

    GraphicsContext gc = mazeArea.getGraphicsContext2D();

    int rows = maze.getRows();
    int cols = maze.getColumns();
    double cellSize = Math.min(500.0 / cols, 500.0 / rows); // размер ячейки
    gc.setStroke(Color.RED); // цвет пути
    gc.setLineWidth(2); // толщина пути - 2 пикселя

    Point prev = path.get(0);
    for (int i = 1; i < path.size(); i++) {
      Point current = path.get(i);

      double x1 = prev.x * cellSize + cellSize / 2;
      double y1 = prev.y * cellSize + cellSize / 2;
      double x2 = current.x * cellSize + cellSize / 2;
      double y2 = current.y * cellSize + cellSize / 2;

      gc.strokeLine(x1, y1, x2, y2);
      prev = current;
    }
  }

  // Валидация точки по координатам
  public boolean isValidPoint(Point point, int rows, int cols) {
    if (point == null)
      return false;
    int col = point.x;
    int row = point.y;

    return col >= 0 && col < cols && row >= 0 && row < rows;
  }

  // Вывод сообщений об ошибке
  public void showErrorAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static void main(String[] args) {
    launch(args);
  }
}