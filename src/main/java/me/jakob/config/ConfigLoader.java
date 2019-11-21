package me.jakob.config;

import me.jakob.GUI.DriverSelector;
import me.jakob.GUI.DropboxSelector;
import me.jakob.GUI.ErrorGUI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File config = new File(System.getProperty("user.home") + "/Songreporter/config.json");
    private ConfigLoader INSTANCE;

    // Configmanager to save me.jakob.config and access the values
    private ConfigManager configManager;



    // function to check if the values in the me.jakob.config file are correct
    public ConfigManager load() {
        // checking if me.jakob.config file exists and creating a new one if not
        try {
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // getting or if needed setting the path to the browserdriver
        String driverPath = configManager.getDriverPath();
        String dropboxPath = configManager.getDropboxPath();
        File testDriverFile;
        File testDropboxFile;
        if (driverPath == null || driverPath.isEmpty()) {
            driverPath = new DriverSelector().selectDriver();
            configManager.setDriverPath(driverPath);
            configManager.saveConfig();
            testDriverFile = new File(driverPath);
        } else {
            testDriverFile = new File(driverPath);
        }
        if (!(testDriverFile.exists())) {
            driverPath = new DriverSelector().selectDriver();
            configManager.setDriverPath(driverPath);
            configManager.saveConfig();
        }

        // getting or if needed setting the path to the users dropbox directory
        if (dropboxPath == null || dropboxPath.isEmpty()) {
            dropboxPath = new DropboxSelector().selectDropbox();
            configManager.setDropboxPath(dropboxPath);
            configManager.saveConfig();
            testDropboxFile = new File(dropboxPath);
        } else {
            testDropboxFile = new File(dropboxPath);
        }
        if (!(testDropboxFile.exists())) {
            dropboxPath = new DropboxSelector().selectDropbox();
            configManager.setDriverPath(dropboxPath);
            configManager.saveConfig();
        }

        return configManager;
    }

    // function to read the me.jakob.config file and create a new configmanager out of this
    private void loadConfig() throws IOException {
        // testing if the me.jakob.config.json File exists and creating a new one if not
        boolean isCreated = true;
        if (!(config.exists())) {
            isCreated = new File(config.getParent()).mkdirs();
            try {
                isCreated = config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            newConfig();
        // testing if the me.jakob.config is empty and creating a new one if it is empty
        } else if (!(new BufferedReader(new InputStreamReader(new FileInputStream(config), StandardCharsets.UTF_8)).ready())) {
            newConfig();
        // reading the file if everything is ok with it
        } else {
            StringBuilder fromJsonBuilder = new StringBuilder();
            BufferedReader bufferedReader;

            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(config), StandardCharsets.UTF_8));
                while (bufferedReader.ready()) {
                    fromJsonBuilder.append(bufferedReader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            configManager = gson.fromJson(fromJsonBuilder.toString().trim(), ConfigManager.class);
        }

        // showing an error window if something went wrong with the me.jakob.config file
        if (!isCreated) {
            ErrorGUI jsonErrorGUI = new ErrorGUI();
            jsonErrorGUI.showNewErrorMessage("Es ist ein Fehler mit der json Datei aufgetreten!");
        }
    }

    // function to create a new and complete me.jakob.config file
    private void newConfig() {
        // new configmanager to set and save the values
        configManager = new ConfigManager();

        // setting the values
        String driverPath = new DriverSelector().selectDriver();
        configManager.setDriverPath(driverPath);

        String dropboxPath = new DropboxSelector().selectDropbox();
        configManager.setDropboxPath(dropboxPath);

        // saving the configuration
        configManager.saveConfig();
    }
}
