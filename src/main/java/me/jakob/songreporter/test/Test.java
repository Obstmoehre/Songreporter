package me.jakob.songreporter.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.jakob.songreporter.GUI.SummaryGUIController;
import me.jakob.songreporter.reporting.Song;

import java.util.ArrayList;

public class Test extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        Test.primaryStage = primaryStage;
    }

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("Testsong"));

        setPrimaryStage(primaryStage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/summaryGUI.fxml"));
        Parent root = fxmlLoader.load();

        SummaryGUIController controller = fxmlLoader.getController();
        controller.summarise(songs);

        primaryStage.setTitle("Summary");
        primaryStage.setScene(new Scene(root, 600, 50));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }
}
