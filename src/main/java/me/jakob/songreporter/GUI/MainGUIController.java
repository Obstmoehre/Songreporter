package me.jakob.songreporter.GUI;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import me.jakob.songreporter.config.ConfigLoader;
import me.jakob.songreporter.config.ConfigManager;
import me.jakob.songreporter.reporting.CCLIReader;
import me.jakob.songreporter.reporting.Reporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MainGUIController implements Initializable {

    private static final ConfigManager configManager = new ConfigLoader().load();
    private File script;

    public MenuButton browserButton;
    public Label scriptLabel;
    public Label dropboxLabel;
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
        configManager.setTempEMail(eMail);
        configManager.setTempPassword(password);
        configManager.saveConfig();

        try {
            new Reporter().report(configManager, new CCLIReader().start(configManager));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (checkScript(configManager.getScript())) {
            scriptLabel.setStyle("-fx-text-fill: #58a832");
        }
    }

    public void onScriptButtonClick() {
        ScriptSelector scriptSelector = new ScriptSelector();
        File script = scriptSelector.selectScript(configManager);

        while(script != null && !script.getName().endsWith(".col")) {
            script = scriptSelector.selectScript(configManager);
            if (script == null) {
                break;
            }
        }

        if (script != null && !checkScript(script)) {
            setLabelText(scriptLabel, script.getName());
            scriptLabel.setStyle("-fx-text-fill: -fx-text-base-color");
            configManager.setScript(script);
        } else if (script != null && script.getName().endsWith(".col") && checkScript(script)) {
            setLabelText(scriptLabel, script.getName());
            scriptLabel.setStyle("-fx-text-fill: #58a832");
            configManager.setScript(script);
        }
    }

    public void onDropboxButtonClick() {
        String dropboxPath = "";
        DropboxSelector dropboxSelector = new DropboxSelector();
        while (dropboxPath.equals("") || !(dropboxPath.endsWith("Dropbox"))) {
            if (!(dropboxPath = dropboxSelector.selectDropbox()).equals("")) {
                setLabelText(dropboxLabel, dropboxPath);
                dropboxLabel.setStyle("-fx-text-fill: -fx-text-base-color");
                if (!dropboxPath.endsWith("Dropbox")) {
                    dropboxLabel.setStyle("-fx-text-fill: #eb4034");
                } else {
                    configManager.setDropboxPath(dropboxPath);
                    configManager.saveConfig();
                }
            } else {
                break;
            }
        }
        setLabelText(dropboxLabel, dropboxPath);
    }

    public void onBrowserButtonClick() {

    }

    private boolean getLatestScript() {
        LocalDate date = LocalDate.now();
        File[] scripts = new File(configManager.getDropboxPath() + "/Songbeamer/Scripts").listFiles();
        if (scripts != null) {
            for (File script : scripts) {
                for (int i = 0; i < 7; i++) {
                    if (script.getName().contains(date.minusDays(i).toString()) && !checkScript(script)) {
                        this.script = script;
                    }
                }

            }
            return script != null;
        } else {
            return false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addBrowserButtonListeners();
        if (configManager.getBrowser() != null && !configManager.getBrowser().equals("")) {
            browserButton.setText(configManager.getBrowser());
        }

        if (getLatestScript()) {
            setLabelText(scriptLabel, script.getName());
            configManager.setScript(script);
        }

        setLabelText(dropboxLabel, configManager.getDropboxPath());

        if (configManager.getEMail() != null) {
            eMailField.setText(configManager.getEMail());
        }
        if (configManager.getPassword() != null) {
            passwordField.setText(configManager.getPassword());
        }
    }

    private void addBrowserButtonListeners() {
        ObservableList<MenuItem> menuItems = browserButton.getItems();
        menuItems.get(0).setOnAction(a -> {
            browserButton.setText("Chrome");
            configManager.setBrowser("Chrome");
        });
        menuItems.get(1).setOnAction(a -> {
            browserButton.setText("Firefox");
            configManager.setBrowser("Firefox");
        });
        menuItems.get(2).setOnAction(a -> {
            browserButton.setText("Opera");
            configManager.setBrowser("Opera");
        });
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
    }

    private boolean checkScript(File script) {
        if (script != null) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(script));
                while (bufferedReader.ready()) {
                    if (bufferedReader.readLine().contains("#reported")) {
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}