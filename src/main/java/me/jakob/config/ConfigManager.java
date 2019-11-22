package me.jakob.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class ConfigManager {
    // variables for saving the path to the dropbox directory and the browserdriver
    private transient Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private transient File config = new File(System.getProperty("user.home") + "/Songreporter/config.json");
    private String driverPath;
    private String dropboxPath;
    private String eMail;
    private String password;

    // function to save the values into the me.jakob.config file
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

    public byte[] checkValues() {
        byte[] valueCorrectness = {1, 1};
        if (this.driverPath != null && new File(this.driverPath).exists()) {
            valueCorrectness[0] = 0;
        }
        if (this.dropboxPath != null && new File(this.dropboxPath).exists() && this.dropboxPath.endsWith("Dropbox")) {
            valueCorrectness[1] = 0;
        }
        return valueCorrectness;
    }

    public String getDriverPath() {
        return this.driverPath;
    }

    public String getDropboxPath() {
        return this.dropboxPath;
    }

    public void setDriverPath(String driverPath) {
        this.driverPath = driverPath;
    }

    public void setDropboxPath(String dropboxPath) {
        this.dropboxPath = dropboxPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }
}
