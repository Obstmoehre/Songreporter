package me.jakob.songreporter.test;

import javafx.application.Application;
import javafx.stage.Stage;
import me.jakob.songreporter.reporting.CCLIReader;
import me.jakob.songreporter.reporting.Reporter;

import java.io.File;
import java.io.IOException;

public class Test extends Application {

    private final Reporter reporter = new Reporter();
    private final CCLIReader ccliReader = new CCLIReader();
    public static Stage primaryStage;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Test.primaryStage = primaryStage;

        for (int i = 0; i < 100; i++) {
            reporter.report("jakob.gerstmann@outlook.de", "Beamer/Jungs", "Chrome",
                    new File("C:\\Users\\jucom.Demo\\Desktop\\Songbeamer\\Scripts\\Ablaufplan 2020-08-30.col"),
                    new boolean[]{false, true, true, false},
                    ccliReader.read("C:\\Users\\jucom.Demo\\Desktop\\Songbeamer\\Songs\\",
                            new File("C:\\Users\\jucom.Demo\\Desktop\\" +
                                    "Songbeamer\\Scripts\\Ablaufplan 2020-08-30.col")));
        }
    }
}
