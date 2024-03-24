package com.example.fifteenpuzzle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
public class FifteenPuzzleApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FifteenPuzzle.fxml"));
        Image icon = new Image(getClass().getResourceAsStream("DFP.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("DynamicFifteenPuZZle");
        primaryStage.setScene(new Scene(root, 1000, 1000));
        double minWindowWidth = 800; // Минимальная ширина окна
        double minWindowHeight = 600; // Минимальная высота окна
        primaryStage.setMinWidth(minWindowWidth);
        primaryStage.setMinHeight(minWindowHeight);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}