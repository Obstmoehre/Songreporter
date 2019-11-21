package me.jakob.GUI;

import me.jakob.config.ConfigLoader;
import me.jakob.config.ConfigManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import me.jakob.reporting.CCLIReader;
import me.jakob.reporting.Reporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ResourceBundle;

public class MainGUIController implements Initializable {

    private static ConfigManager configManager = new ConfigLoader().load();
    private File script;

    public Pane samplePane;
    public Label scriptLabel;
    public Label dropboxLabel;
    public Label driverLabel;
    public TextField eMailField;
    public PasswordField passwordField;
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

        configManager.saveConfig();

        // reading the ccli songnumbers out of the script
        //ArrayList<String> ccliList = new CCLIReader().start(configManager, script);

        // open browser and report the given ccli songnumbers
        //new Reporter().report(configManager, ccliList, eMail, password);
    }

    public void onScriptButtonClick() {
        while (script == null || !(script.getName().endsWith(".col"))) {
            JFileChooser scriptChooser = new JFileChooser();
            scriptChooser.setPreferredSize(new Dimension(900, 700));
            scriptChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            // setting the starting directory of the me.jakob.GUI to the scripts directory in the dropbox if it exists
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
        setLabelText(dropboxLabel, dropboxPath);

        configManager.setDropboxPath(dropboxPath);
        configManager.saveConfig();
    }

    public void onDriverButtonClick() {
        String driverPath = new DriverSelector().selectDriver();
        setLabelText(driverLabel, driverPath);

        configManager.setDriverPath(driverPath);
        configManager.saveConfig();
    }

    private void setLabelText(Label label, String text) {
        if (text.length() > 40) {
            StringBuilder newTextBuilder = new StringBuilder();
            for (int i = text.length() / 2; i < text.length(); i++) {
                if (text.charAt(i) == '\\') {
                    newTextBuilder.append(text, 0, i);
                    newTextBuilder.append("\n");
                    newTextBuilder.append(text.substring(i));
                    text = newTextBuilder.toString();
                    break;
                }
            }
        }
        label.setText(text);
        label.setLayoutX((samplePane.getWidth() - label.getWidth())/2);
    }

    private boolean getLatestScript() {
        LocalDate date = LocalDate.now();
        File[] scripts = new File(configManager.getDropboxPath() + "/Songbeamer/Scripts").listFiles();
        if (scripts != null) {
            for (File script : scripts) {
                for (int i = 0; i < 7; i++) {
                    if (script.getName().contains(date.minusDays(i).toString())) {
                        this.script = script;
                    }
                }

            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (getLatestScript()) {
            setLabelText(scriptLabel, script.getName());
        }

        setLabelText(dropboxLabel, configManager.getDropboxPath());
        setLabelText(driverLabel, configManager.getDriverPath());

        if (configManager.getEMail() != null) {
            eMailField.setText(configManager.getEMail());
        }
        if (configManager.getPassword() != null) {
            passwordField.setText(configManager.getPassword());
        }
    }

    public void alignLabels() {
        scriptLabel.setLayoutX((samplePane.getWidth() - scriptLabel.getWidth())/2);
        dropboxLabel.setLayoutX((samplePane.getWidth() - dropboxLabel.getWidth())/2);
        driverLabel.setLayoutX((samplePane.getWidth() - driverLabel.getWidth())/2);
    }
}
