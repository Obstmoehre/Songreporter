package GUI;

import config.ConfigLoader;
import config.ConfigManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import reporting.CCLIReader;
import reporting.Reporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainGUIController implements Initializable {

    private static ConfigManager configManager = new ConfigLoader().load();
    private File script;

    @FXML
    public Pane samplePane;

    @FXML
    public Label scriptLabel;

    @FXML
    public Label dropboxDirectoryLabel;

    @FXML
    public Label driverLabel;

    public void onReportButtonClick() {
        // reading the ccli songnumbers out of the script
        ArrayList<String> ccliList = new CCLIReader().start(configManager, script);

        // open browser and report the given ccli songnumbers
        new Reporter().report(configManager, ccliList);
    }

    public void onScriptButtonClick() {
        while (script == null || !(script.getName().endsWith(".col"))) {
            JFileChooser scriptChooser = new JFileChooser();
            scriptChooser.setPreferredSize(new Dimension(900, 700));
            scriptChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            // setting the starting directory of the GUI to the scripts directory in the dropbox if it exists
            File standardScriptsDirectory = new File(configManager.getDropboxPath() + "/SongBeamer/Scripts");
            if (standardScriptsDirectory.exists()) {
                scriptChooser.setCurrentDirectory(standardScriptsDirectory);
            } else {
                scriptChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            }
            scriptChooser.setDialogTitle("Ablaufplan auswählen");
            scriptChooser.showDialog(null, "Auswählen");
            if (scriptChooser.getSelectedFile() != null) {
                script = scriptChooser.getSelectedFile();
                scriptLabel.setText(script.getName());
                scriptLabel.setStyle("-fx-text-fill: #000000");
                scriptLabel.setLayoutX((samplePane.getWidth() - scriptLabel.getWidth())/2);
            } else {
                break;
            }
        }

        if (!script.getName().endsWith(".col")) {
            scriptLabel.setStyle("-fx-text-fill: #eb4034");
        }
    }

    public void onDropboxButtonClick() {
        String dropboxPath = new DropboxSelector().selectDropbox();
        dropboxDirectoryLabel.setText(dropboxPath);
        dropboxDirectoryLabel.setLayoutX((samplePane.getWidth() - dropboxDirectoryLabel.getWidth())/2);
        configManager.setDropboxPath(dropboxPath);
        configManager.saveConfig();
    }

    public void onDriverButtonClick() {
        String driverPath = new DriverSelector().selectDriver();
        configManager.setDriverPath(driverPath);
        if (driverPath.length() > 30) {
            StringBuilder newDriverPathBuilder = new StringBuilder();
            for (int i = driverPath.length() / 2; i < driverPath.length(); i++) {
                if (driverPath.charAt(i) == '\\') {
                    newDriverPathBuilder.append(driverPath, 0, i-1);
                    newDriverPathBuilder.append("\n");
                    newDriverPathBuilder.append(driverPath.substring(i-1));
                    driverPath = newDriverPathBuilder.toString();
                    break;
                }
            }
            driverLabel.setText(driverPath);
            driverLabel.setLayoutX((samplePane.getWidth() - driverLabel.getWidth())/4);
        } else {
            driverLabel.setText(driverPath);
            driverLabel.setLayoutX((samplePane.getWidth() - driverLabel.getWidth())/2);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dropboxDirectoryLabel.setText(configManager.getDropboxPath());
        dropboxDirectoryLabel.setLayoutX((samplePane.getWidth() - dropboxDirectoryLabel.getWidth())/2);
        driverLabel.setText(configManager.getDriverPath());
        driverLabel.setLayoutX((samplePane.getWidth() - driverLabel.getWidth())/2);
    }
}
