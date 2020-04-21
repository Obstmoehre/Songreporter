package me.jakob.songreporter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.jakob.songreporter.GUI.MainGUIController;

public class Songreporter extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainGUI.fxml"));
        Parent root = fxmlLoader.load();

        MainGUIController mainGUIController = fxmlLoader.getController();

        primaryStage.setTitle("Songreporter");
        primaryStage.setScene(new Scene(root, 920, 192));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();

        mainGUIController.alignLabels();
    }
}
