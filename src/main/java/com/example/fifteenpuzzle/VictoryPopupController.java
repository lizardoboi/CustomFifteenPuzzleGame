package com.example.fifteenpuzzle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class VictoryPopupController {
    private FifteenPuzzleController gameController;
    private Stage stage;
    private int gridSize; // Поле для хранения размера игрового поля

    public void setGameController(FifteenPuzzleController gameController) {
        this.gameController = gameController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        Image icon = new Image(getClass().getResourceAsStream("bigwin.jpg"));
        stage.getIcons().add(icon);
        stage.setResizable(false);
    }

    // Метод для установки размера игрового поля
    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    @FXML
    private void restartGame(ActionEvent event) {
        // Закрываем окно VictoryPopup
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

        // Убедитесь, что gameController установлен и не равен null
        if (gameController != null) {
            Platform.runLater(() -> {
                gameController.showGridSizeInputDialog();
            });
        } else {
            System.err.println("gameController не установлен.");
        }
    }

    @FXML
    private void exitGame(ActionEvent event) {
        stage.close(); // Закрываем окно
        gameController.getStage().close();
    }

}