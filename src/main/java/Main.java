import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/mainGUI.fxml"));
        primaryStage.setTitle("Songreporter");
        primaryStage.setScene(new Scene(root, 920, 197));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }
}
