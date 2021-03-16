package me.jakob.songreporter.GUI.controller;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import me.jakob.songreporter.GUI.elements.DirectorySelector;
import me.jakob.songreporter.GUI.elements.FileSelector;
import me.jakob.songreporter.config.Config;
import me.jakob.songreporter.config.ConfigManager;
import me.jakob.songreporter.reporting.Reporter;
import me.jakob.songreporter.reporting.enums.Category;
import me.jakob.songreporter.reporting.objects.Categories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MainGUIController implements Initializable {

    private final ConfigManager configManager = ConfigManager.getInstance();
    private final Config config = configManager.getConfig();
    private final Reporter reporter = new Reporter();
    private File script;

    public TextField eMailField;
    public PasswordField passwordField;
    public CheckBox saveCheckBox;
    public MenuButton browserButton;
    public CheckBox printBox;
    public CheckBox digitalBox;
    public CheckBox streamBox;
    public CheckBox translationBox;
    public Label songsLabel;
    public Label scriptsLabel;
    public Label scriptLabel;

    public void onReportButtonClick() {
        if (checkInformation()) {
            if (saveCheckBox.isSelected()) {
                this.configManager.setSaveCredentialsMode(true);
            } else if (eMailField.getText().equals(this.config.getEMail())) {
                this.configManager.setSaveCredentialsMode(false);
            }
            this.configManager.setEMail(this.eMailField.getText());
            this.configManager.setPassword(this.passwordField.getText());
            this.configManager.saveConfig();
            this.reporter.report(this.config, this.script);

            if (checkScript(script)) {
                scriptLabel.setStyle("-fx-text-fill: #58a832");
            }
        }
    }

    private boolean checkInformation() {
        boolean vaildInformation = true;
        String browser = this.config.getBrowser();
        Label[] directoryLabels = {this.songsLabel, this.scriptsLabel};

        if (browser == null || !(browser.equals("Chrome") || browser.equals("Firefox") || browser.equals("Opera"))) {
            this.browserButton.setTextFill(new Color(1, 0, 0, 1.0));
            vaildInformation = false;
        }
        for (Label label : directoryLabels) {
            if (label.getText().equals("none") || !(new File(label.getText().replace("/", "\\\\").replace("\n", "")).exists())) {
                label.setStyle("-fx-text-fill: #ff0000");
                vaildInformation = false;
            }
        }

        if (this.script == null || !this.script.exists()) {
            this.scriptLabel.setStyle("-fx-text-fill: #ff0000");
            vaildInformation = false;
        }

        if (this.eMailField.getText().isEmpty()) {
            this.eMailField.setStyle("-fx-border-color: #ff0000");
            vaildInformation = false;
        }
        if (this.passwordField.getText().isEmpty()) {
            this.passwordField.setStyle("-fx-border-color: #ff0000");
            vaildInformation = false;
        }

        return vaildInformation;
    }

    public void onScriptButtonClick() {
        File script = new FileSelector("Choose Flowsheet", config.getScriptsDirectory()).select();

        if (!checkScript(script) && script != null) {
            setLabelText(scriptLabel, script.getName());
            scriptLabel.setStyle("-fx-text-fill: -fx-text-base-color");
            this.script = script;
        } else if (checkScript(script)) {
            setLabelText(scriptLabel, script.getName());
            scriptLabel.setStyle("-fx-text-fill: #58a832");
            this.script = script;
        }
    }

    private boolean checkScript(File script) {
        if (script != null && script.getName().endsWith(".col")) {
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

    public void onSongsButtonClick() {
        String songsDirectory;
        DirectorySelector songsSelector = new DirectorySelector("Choose Songs Directory");
        if ((songsDirectory = songsSelector.select()) != null) {
            setLabelText(songsLabel, songsDirectory);
            songsLabel.setStyle("-fx-text-fill: -fx-text-base-color");
            this.configManager.setSongsDirectory(songsDirectory);
            this.configManager.saveConfig();
        }
    }

    public void onScriptsButtonClick() {
        String scriptsDirectory;
        DirectorySelector songsSelector = new DirectorySelector("Choose Scripts Directory");
        if ((scriptsDirectory = songsSelector.select()) != null) {
            setLabelText(scriptsLabel, scriptsDirectory);
            songsLabel.setStyle("-fx-text-fill: -fx-text-base-color");
            this.configManager.setScriptsDirectory(scriptsDirectory);
            this.configManager.saveConfig();
        }
    }

    public void onEMailFieldReleased() {
        if (this.eMailField.getText().isEmpty()) {
            this.eMailField.setStyle("-fx-border-color: #ff0000");
        } else {
            this.eMailField.setStyle("-fx-border-color: -fx-border-base-color");
        }
    }

    public void onPasswordFieldReleased() {
        if (this.passwordField.getText().isEmpty()) {
            this.passwordField.setStyle("-fx-border-color: #ff0000");
        } else {
            this.passwordField.setStyle("-fx-border-color: -fx-border-base-color");
        }
    }

    public void onPrintChange() {
        configManager.setCategory(Category.PRINT, this.printBox.isSelected());
    }

    public void onDigitalChange() {
        configManager.setCategory(Category.DIGITAL, this.digitalBox.isSelected());
    }

    public void onStreamChange() {
        configManager.setCategory(Category.STREAM, this.streamBox.isSelected());
    }

    public void onTranslationChange() {
        configManager.setCategory(Category.TRANSLATION, this.translationBox.isSelected());
    }

    public void onSaveConfigClick() {
        this.configManager.saveConfig();
    }

    private void addBrowserButtonListeners() {
        ObservableList<MenuItem> menuItems = browserButton.getItems();
        menuItems.get(0).setOnAction(a -> {
            browserButton.setText("Chrome");
            this.configManager.setBrowser("Chrome");
        });
        menuItems.get(1).setOnAction(a -> {
            browserButton.setText("Firefox");
            this.configManager.setBrowser("Firefox");
        });
        menuItems.get(2).setOnAction(a -> {
            browserButton.setText("Opera");
            this.configManager.setBrowser("Opera");
        });
    }

    private File getLatestScript() {
        LocalDate date = LocalDate.now();
        File[] scripts = new File(this.config.getScriptsDirectory()).listFiles();
        if (scripts != null) {
            for (File script : scripts) {
                for (int i = 0; i < 7; i++) {
                    if (script.getName().contains(date.minusDays(i).toString()) && !checkScript(script)) {
                        return script;
                    }
                }

            }
        }
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addBrowserButtonListeners();
        if (this.config.getBrowser() != null) {
            browserButton.setText(this.config.getBrowser());
        }

        Categories categories = this.config.getCategories();
        this.printBox.setSelected(categories.getPrint().equals("1"));
        this.digitalBox.setSelected(categories.getDigital().equals("1"));
        this.streamBox.setSelected(categories.getRecord().equals("1"));
        this.translationBox.setSelected(categories.getTranslate().equals("1"));

        if (this.config.getScriptsDirectory() != null) {
            script = getLatestScript();
        }
        if (script != null) {
            setLabelText(scriptLabel, script.getName());
        }

        if (this.config.getScriptsDirectory() != null) {
            setLabelText(scriptsLabel, this.config.getScriptsDirectory());
        } else {
            setLabelText(scriptsLabel, "none");
        }

        if (this.config.getSongsDirectory() != null) {
            setLabelText(songsLabel, this.config.getSongsDirectory());
        } else {
            setLabelText(songsLabel, "none");
        }

        if (this.config.getEMail() != null) {
            eMailField.setText(this.config.getEMail());
        }
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
}
