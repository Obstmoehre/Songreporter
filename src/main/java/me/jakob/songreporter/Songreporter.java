package me.jakob.songreporter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Songreporter extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        Songreporter.primaryStage = primaryStage;
    }

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        setPrimaryStage(primaryStage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainGUI.fxml"));
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("Songreporter");
        primaryStage.setScene(new Scene(root, 920, 263));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }
}
