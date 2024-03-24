module com.example.fifteenpuzzle {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.fifteenpuzzle to javafx.fxml;
    exports com.example.fifteenpuzzle;
}