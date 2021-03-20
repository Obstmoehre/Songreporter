package me.jakob.songreporter.config;

import com.google.gson.*;
import me.jakob.songreporter.GUI.elements.ErrorGUI;
import me.jakob.songreporter.reporting.enums.Category;
import me.jakob.songreporter.reporting.objects.Categories;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigManager {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
            config.setCategories(new Categories());
        }
    }

    public Config getConfig() {
        return this.config;
    }

    public void setBrowser(String browser) {
        this.config.setBrowser(browser);
    }

    public void setSongsDirectory(String songsDirectory) {
        this.config.setSongsDirectory(songsDirectory);
    }

    public void setEMail(String eMail) {
        this.config.setEMail(eMail);
    }

    public void setPassword(String password) {
        this.config.setPassword(password);
    }

    public void setScriptsDirectory(String scriptsDirectory) {
        this.config.setScriptsDirectory(scriptsDirectory);
    }

    public void setCategories(Categories categories) {
        this.config.setCategories(categories);
    }

    public void setCategory(Category category, boolean value) {
        String stringValue;
        if (value) {
            stringValue = "1";
        } else {
            stringValue = "0";
        }

        switch (category) {
            case PRINT:
                this.config.getCategories().setPrint(stringValue);
                break;
            case DIGITAL:
                this.config.getCategories().setDigital(stringValue);
                break;
            case STREAM:
                this.config.getCategories().setRecord(stringValue);
                break;
            case TRANSLATION:
                this.config.getCategories().setTranslate(stringValue);
                break;
        }
    }

    public void setSaveCredentialsMode(boolean shouldSave) {
        if (!shouldSave) {
            this.gson = new GsonBuilder().setPrettyPrinting().setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return fieldAttributes.getName().equals("eMail") ||
                            fieldAttributes.getName().equals("password");
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            }).create();
        } else {
            this.gson = new GsonBuilder().setPrettyPrinting().create();
        }
    }
}
