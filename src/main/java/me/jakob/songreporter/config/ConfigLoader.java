package me.jakob.songreporter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import me.jakob.songreporter.GUI.elements.ErrorGUI;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File config = new File(System.getProperty("user.home") + "/Songreporter/config.json");

    private ConfigManager configManager;

    public ConfigManager load() {
        try {
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkConfigValues();

        return configManager;
    }

    private void loadConfig() throws IOException {
        boolean isCreated = true;
        if (!(config.exists())) {
            isCreated = new File(config.getParent()).mkdirs();
            try {
                isCreated = config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            newConfig();
        } else if (!(new BufferedReader(new InputStreamReader(new FileInputStream(config), StandardCharsets.UTF_8)).ready())) {
            newConfig();
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

            try {
                configManager = gson.fromJson(fromJsonBuilder.toString().trim(), ConfigManager.class);
            } catch (JsonSyntaxException e) {
                newConfig();
            }
        }

        if (!isCreated) {
            ErrorGUI jsonErrorGUI = new ErrorGUI();
            jsonErrorGUI.showNewErrorMessage("Config file couldn't be created.");
        }
    }

    private void newConfig() {
        configManager = new ConfigManager();
        configManager.saveConfig();
    }

    private void checkConfigValues() {
        String browser = configManager.getBrowser();
        if (browser != null && !(browser.equals("Chrome") || browser.equals("Opera") || browser.equals("Firefox"))) {
            configManager.setBrowser(null);
        }

        String songsPath = configManager.getSongsDirectory();
        if (songsPath != null) {
            File testSongsFile = new File(songsPath);
            if (!(testSongsFile.exists())) {
                configManager.setSongsDirectory(null);
            }
        }

        String scriptsPath = configManager.getScriptsDirectory();
        if (scriptsPath != null) {
            File testScriptsFile = new File(scriptsPath);
            if (!(testScriptsFile.exists())) {
                configManager.setScriptsDirectory(null);
            }
        }

        if (configManager.getCategories() == null) {
            configManager.setCategories(new boolean[]{false, false, false, false});
        }
    }
}
