package me.jakob.songreporter.GUI.elements;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ErrorGUI {
    public void showNewErrorMessage(String message) {
        Stage primaryStage = new Stage();
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);

        Label label = new Label(message);

        root.setCenter(label);
        root.setMinSize(300,200);
        root.setVisible(true);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
