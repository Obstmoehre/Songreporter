package me.jakob.songreporter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class ConfigManager {
    private final transient Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final transient File config = new File(System.getProperty("user.home") + "/Songreporter/config.json");
    private String browser;
    private String songsDirectory;
    private String scriptsDirectory;
    private String eMail;
    private boolean[] categories;

    public void saveConfig() {
        String toJson = this.gson.toJson(this);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.config)));
            bufferedWriter.write(toJson);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBrowser() {
        return this.browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getSongsDirectory() {
        return this.songsDirectory;
    }

    public void setSongsDirectory(String sonbeamerPath) {
        this.songsDirectory = sonbeamerPath;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

    public String getScriptsDirectory() {
        return scriptsDirectory;
    }

    public void setScriptsDirectory(String scriptsDirectory) {
        this.scriptsDirectory = scriptsDirectory;
    }

    public boolean[] getCategories() {
        return categories;
    }

    public void setCategories(boolean[] categories) {
        this.categories = categories;
    }
}
