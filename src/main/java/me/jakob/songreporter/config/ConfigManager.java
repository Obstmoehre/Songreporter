package me.jakob.songreporter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import me.jakob.songreporter.GUI.elements.ErrorGUI;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile = new File(System.getProperty("user.home") + "/Songreporter/config.json");
    private Config config;
    private static ConfigManager instance;

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private ConfigManager() {
        Config config = null;
        try {
            config = loadConfig();
            checkConfigValues(config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (config != null) {
            this.config = config;
        }
    }

    public void saveConfig() {
        String toJson = this.gson.toJson(config);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.configFile)));
            bufferedWriter.write(toJson);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Config loadConfig() throws IOException {
        boolean isCreated = true;
        if (!(configFile.exists())) {
            isCreated = new File(configFile.getParent()).mkdirs();
            try {
                isCreated = configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Config();
        } else if (!(new BufferedReader(new InputStreamReader(
                new FileInputStream(configFile), StandardCharsets.UTF_8)
        ).ready())) {
            return new Config();
        } else {
            StringBuilder fromJsonBuilder = new StringBuilder();
            BufferedReader bufferedReader;

            try {
                bufferedReader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(configFile), StandardCharsets.UTF_8)
                );
                while (bufferedReader.ready()) {
                    fromJsonBuilder.append(bufferedReader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                return gson.fromJson(fromJsonBuilder.toString().trim(), Config.class);
            } catch (JsonSyntaxException e) {
                return new Config();
            }
        }
    }

    private void checkConfigValues(Config config) {
        String browser = config.getBrowser();
        if (browser != null && !(browser.equals("Chrome") || browser.equals("Opera") || browser.equals("Firefox"))) {
            config.setBrowser(null);
        }

        String songsPath = config.getSongsDirectory();
        if (songsPath != null) {
            File testSongsFile = new File(songsPath);
            if (!(testSongsFile.exists())) {
                config.setSongsDirectory(null);
            }
        }

        String scriptsPath = config.getScriptsDirectory();
        if (scriptsPath != null) {
            File testScriptsFile = new File(scriptsPath);
            if (!(testScriptsFile.exists())) {
                config.setScriptsDirectory(null);
            }
        }

        if (config.getCategories() == null) {
            config.setCategories(new boolean[]{false, false, false, false});
        }
    }

    public String getBrowser() {
        return this.config.getBrowser();
    }

    public void setBrowser(String browser) {
        this.config.setBrowser(browser);
    }

    public String getSongsDirectory() {
        return this.config.getSongsDirectory();
    }

    public void setSongsDirectory(String songsDirectory) {
        this.config.setSongsDirectory(songsDirectory);
    }

    public String getEMail() {
        return this.config.getEMail();
    }

    public void setEMail(String eMail) {
        this.config.setEMail(eMail);
    }

    public String getScriptsDirectory() {
        return this.config.getScriptsDirectory();
    }

    public void setScriptsDirectory(String scriptsDirectory) {
        this.config.setScriptsDirectory(scriptsDirectory);
    }

    public boolean[] getCategories() {
        return this.config.getCategories();
    }

    public void setCategories(boolean[] categories) {
        this.config.setCategories(categories);
    }
}
