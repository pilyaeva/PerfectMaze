#!/bin/sh
JAVAFX_LIB_DIR="lib/javafx"
CLASSPATH="$(dirname "$0")/lib"
java -cp "$CLASSPATH" --module-path "$JAVAFX_LIB_DIR" --add-modules javafx.controls,javafx.fxml,javafx.graphics view.MazeApplication
