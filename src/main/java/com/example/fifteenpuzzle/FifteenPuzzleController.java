package com.example.fifteenpuzzle;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FifteenPuzzleController {
    private int gridSize;
    @FXML
    private GridPane gridPane;
    private Button[][] buttons;
    private int[][] board;
    private int emptyRow; // Индекс строки пустой клетки
    private int emptyCol; // Индекс столбца пустой клетки


    public void initialize() {
        System.out.println("Initializing...");
        gridSize = (int) Math.sqrt(gridPane.getChildren().size());
        try {
            showGridSizeInputDialog();
            initializeBoard(gridSize);
        } catch (Exception e) {
            e.printStackTrace();
            // Обработка ошибки инициализации доски
        }
    }
    public void initializeBoard(int gridSize) {
        this.gridSize = gridSize; // Установка нового значения gridSize
        int boardSize = gridSize * gridSize;

        // Очистите GridPane перед добавлением новых элементов (если это необходимо)
        gridPane.getChildren().clear();

        // Обновление размера массивов и кнопок в соответствии с gridSize
        buttons = new Button[gridSize][gridSize];
        board = new int[gridSize][gridSize];

        // Высота и ширина кнопок для отображения значений
        double buttonWidth = 60;
        double buttonHeight = 60;

        // Создание и добавление кнопок в GridPane
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                buttons[row][col] = new Button();
                buttons[row][col].setPrefSize(buttonWidth, buttonHeight); // Установка размера кнопок
                buttons[row][col].setFont(Font.font("System", FontWeight.BOLD, 12));
                buttons[row][col].setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");
                gridPane.add(buttons[row][col], col, row);
                buttons[row][col].setMinSize(30, 30); // Минимальный размер кнопок
                buttons[row][col].setMaxSize(100, 100); // Максимальный размер кнопок

                Button restartButton = new Button("Reroll");
                restartButton.setPrefSize(buttonWidth, buttonHeight);
                restartButton.setFont(Font.font("System", FontWeight.BOLD, 10));
                restartButton.setStyle("-fx-background-color: gray;");
                gridPane.add(restartButton, 0, gridSize);
                restartButton.setOnAction(e -> handleRestart(e));

                Button restartGridButton = new Button("Diff");
                restartGridButton.setPrefSize(buttonWidth, buttonHeight);
                restartGridButton.setFont(Font.font("System", FontWeight.BOLD, 10));
                restartGridButton.setStyle("-fx-background-color: gray;");
                gridPane.add(restartGridButton, 1, gridSize);
                restartGridButton.setOnAction(e -> handleRestartGrid(e));

                // Добавление обработчика события к кнопке
                buttons[row][col].setOnAction(e -> handleButtonClick(e));
            }
        }

        do {
            List<Integer> numbers = new ArrayList<>();

            // Заполнение списка числами от 0 до boardSize - 1 (0 представляет пустую клетку)
            for (int i = 0; i < boardSize; i++) {
                numbers.add(i);
            }

            // Перемешивание чисел в списке
            Collections.shuffle(numbers);

            // Заполнение игрового поля (board) из списка чисел
            int index = 0;
            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    board[row][col] = numbers.get(index++);
                }
            }

            // Поиск пустой клетки и установка emptyRow и emptyCol
            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    if (board[row][col] == 0) {
                        emptyRow = row;
                        emptyCol = col;
                        break;
                    }
                }
            }

            // Обновление текста на кнопках
            updateButtonLabels();
        } while (!isSolvable());
    }
    public void showGridSizeInputDialog() {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Выбор размера сетки");
        dialog.setHeaderText("Введите размер сетки (целое число):");
        // Создаем кнопку "Далее"
        ButtonType nextButtonType = new ButtonType("Далее");
        dialog.getDialogPane().getButtonTypes().add(nextButtonType);

        // Создаем текстовое поле для ввода размера сетки
        TextField gridSizeField = new TextField("1");
        Label gridSizeLabel = new Label("Размер сетки:");

        // Создаем сетку для размещения элементов
        GridPane grid = new GridPane();
        grid.add(gridSizeLabel, 1, 1);
        grid.add(gridSizeField, 2, 1);
        Image icon = new Image(getClass().getResourceAsStream("diff.png"));
        // Добавляем сетку в диалог
        dialog.getDialogPane().setContent(grid);

        // Устанавливаем обработчик кнопки "Далее"
        dialog.setResultConverter(buttonType -> {
            if (buttonType == nextButtonType) {
                return new Pair<>(gridSizeField.getText(), null);
            }
            return null;
        });

        // Ожидание ввода от пользователя и обработка результата
        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (result.isPresent()) {
            String size = result.get().getKey();
            try {
                int gridSize = Integer.parseInt(size);
                if (gridSize <= 0) {
                    // Если введено нулевое или отрицательное число, выводим сообщение об ошибке
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(null);
                    alert.setContentText("Размер сетки должен быть положительным целым числом.");
                    alert.showAndWait();
                    showGridSizeInputDialog(); // Повторно вызываем диалог
                } else {
                    initializeBoard(gridSize); // Инициализация игровой доски с введенным размером сетки
                }
            } catch (NumberFormatException e) {
                // Обработка ошибки ввода
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Размер сетки должен быть положительным целым числом.");
                alert.showAndWait();
                showGridSizeInputDialog(); // Повторно вызываем диалог
            }
        } else {
            // Если пользователь закрыл диалог, выходите из приложения
            Platform.exit();
        }
    }
    private void updateButtonLabels() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int value = board[row][col];
                String text = (value == 0) ? "" : String.valueOf(value);
                Button button = buttons[row][col];
                String currentText = button.getText();

                if (!text.equals(currentText)) {
                    fadeOutButtonWithAnimation(button, text);
                }

            }
        }
    }

    private void handleButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource(); // Получение нажатой кнопки
        int col = GridPane.getColumnIndex(clickedButton);
        int row = GridPane.getRowIndex(clickedButton);

        // Проверка можно ли переместить плитку
        if (isValidMove(row, col)) {
            // Обмен значениями плиток (board) и обновление индексов emptyRow и emptyCol
            int temp = board[row][col];
            board[row][col] = board[emptyRow][emptyCol];
            board[emptyRow][emptyCol] = temp;

            // Обновление индексов пустой клетки
            emptyRow = row;
            emptyCol = col;

            // Обновление текста на кнопках (значениях)
            updateButtonLabels();
        }

        // Проверка завершилась ли игра (если нужно)
        if (isGameComplete()) {
            showVictoryPopup();
        }
    }

    private boolean isValidMove(int row, int col) {
        // Проверка на наличие пустой клетки вокруг плитки
        if (Math.abs(emptyRow - row) + Math.abs(emptyCol - col) == 1) {
            return true;
        }

        // Если условия выше не выполнились, то ход недопустим
        return false;
    }

    private boolean isGameComplete() {
        int[][] correctBoard = new int[gridSize][gridSize];
        int value = 1;

        // Заполняем правильное состояние числами от 1 до gridSize * gridSize - 1
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                correctBoard[row][col] = value;
                value++;
            }
        }

        correctBoard[gridSize - 1][gridSize - 1] = 0;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (board[row][col] != correctBoard[row][col]) {
                    return false;
                }
            }
        }

        return true;
    }
    public void showVictoryPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VictoryPopup.fxml"));
            Parent root = loader.load();
            // Получаем контроллер окна победы
            VictoryPopupController victoryPopupController = loader.getController();
            victoryPopupController.setGridSize(gridSize);
            // Передаем ссылку на контроллер игры
            victoryPopupController.setGameController(this);

            // Создаем и настраиваем сцену для окна победы
            Scene scene = new Scene(root);
            Stage victoryPopupStage = new Stage();
            victoryPopupStage.initModality(Modality.APPLICATION_MODAL);
            victoryPopupStage.setScene(scene);

            // Устанавливаем ссылку на текущее окно победы в контроллер окна победы
            victoryPopupController.setStage(victoryPopupStage);

            victoryPopupStage.showAndWait(); // Показываем окно и ждем, пока оно закроется
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isSolvable() {
        int inversions = 0;
        int[] flattenedBoard = new int[gridSize * gridSize];

        // Вытаскиваем числа с игровой доски в одномерный массив
        int index = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                flattenedBoard[index++] = board[row][col];
            }
        }

        // Вычисляем количество инверсий
        for (int i = 0; i < gridSize * gridSize - 1; i++) { // Изменяем границу цикла
            for (int j = i + 1; j < gridSize * gridSize; j++) { // Изменяем границу цикла
                if (flattenedBoard[i] > flattenedBoard[j] && flattenedBoard[i] != 0 && flattenedBoard[j] != 0) {
                    inversions++;
                }
            }
        }

        // Если размер сетки нечетный и количество инверсий четное, то конфигурация решаема
        // Если размер сетки четный и количество инверсий четное, то конфигурация также считается решаемой
        return (gridSize % 2 == 1 && inversions % 2 == 0) || (gridSize % 2 == 0 && inversions % 2 == 0);
    }
    private void fadeOutButtonWithAnimation(Button button, String newText) {
        String currentText = button.getText();
        button.setText(newText); // Устанавливаем новое значение текста
        button.setOpacity(0.0); // Устанавливаем начальную прозрачность в ноль

        if (!newText.equals(currentText)) {
            // Создаем анимацию плавного затухания
            FadeTransition fadeOut = new FadeTransition(Duration.millis(750), button);
            fadeOut.setFromValue(0.0); // Начальная прозрачность
            fadeOut.setToValue(1.0); // Заканчиваемая прозрачность
            fadeOut.setOnFinished(event -> {
                // По завершении анимации не нужно ничего делать
            });

            fadeOut.play(); // Запуск анимации
        }
    }
    public void clearButtonValues() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                buttons[row][col].setText(""); // Очищаем текст на кнопке
            }
        }
    }
    @FXML
    public void handleRestart(ActionEvent event) {
        clearButtonValues();
        initializeBoard(gridSize);
    }
    @FXML
    public void handleRestartGrid(ActionEvent event) {
        showGridSizeInputDialog();
    }
    public Stage getStage() {
        return (Stage) gridPane.getScene().getWindow();
    }
}