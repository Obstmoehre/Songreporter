package GUI;

import config.ConfigLoader;
import config.ConfigManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    public Label dropboxLabel;

    @FXML
    public Label driverLabel;

    @FXML
    public TextField eMailField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public CheckBox saveCheckBox;

    public void onReportButtonClick() {
        String eMail = eMailField.getText();
        String password = passwordField.getText();
        if (saveCheckBox.isSelected()) {
            configManager.setEMail(eMail);
            configManager.setPassword(password);
        } else if (eMailField.getText().equals(configManager.getEMail()) &&
                passwordField.getText().equals(configManager.getPassword())) {
            configManager.setEMail("");
            configManager.setPassword("");
        }

        // reading the ccli songnumbers out of the script
        ArrayList<String> ccliList = new CCLIReader().start(configManager, script);

        // open browser and report the given ccli songnumbers
        new Reporter().report(configManager, ccliList, eMail, password);
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
        configManager.setDropboxPath(dropboxPath);
        setLabelText(dropboxLabel, dropboxPath);
    }

    public void onDriverButtonClick() {
        String driverPath = new DriverSelector().selectDriver();
        configManager.setDriverPath(driverPath);
        setLabelText(driverLabel, driverPath);
    }

    private void setLabelText(Label label, String text) {
        if (text.length() > 30) {
            StringBuilder newDriverPathBuilder = new StringBuilder();
            for (int i = text.length() / 2; i < text.length(); i++) {
                if (text.charAt(i) == '\\') {
                    newDriverPathBuilder.append(text, 0, i);
                    newDriverPathBuilder.append("\n");
                    newDriverPathBuilder.append(text.substring(i));
                    text = newDriverPathBuilder.toString();
                    break;
                }
            }
            label.setText(text);
            label.setLayoutX((samplePane.getWidth() - label.getWidth()/2)/2);
        } else {
            label.setText(text);
            label.setLayoutX((samplePane.getWidth() - label.getWidth())/2);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabelText(dropboxLabel, configManager.getDropboxPath());
        setLabelText(driverLabel, configManager.getDriverPath());

        if (configManager.getEMail() != null) {
            eMailField.setText(configManager.getEMail());
        }
        if (configManager.getPassword() != null) {
            passwordField.setText(configManager.getPassword());
        }
    }
}
