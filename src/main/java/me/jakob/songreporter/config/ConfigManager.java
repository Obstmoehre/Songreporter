package me.jakob.songreporter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class ConfigManager {
    // variables for saving the path to the dropbox directory and the browserdriver
    private final transient Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final transient File config = new File(System.getProperty("user.home") + "/Songreporter/config.json");
    private transient File script;
    private transient String tempEMail;
    private transient String tempPassword;
    private String browser;
    private String dropboxPath;
    private String eMail;
    private String password;

    // function to save the values into the me.jakob.songreporter.config file
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

    public String getDropboxPath() {
        return this.dropboxPath;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
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

    public File getScript() {
        return script;
    }

    public void setScript(File script) {
        this.script = script;
    }

    public String getTempEMail() {
        return tempEMail;
    }

    public void setTempEMail(String tempEMail) {
        this.tempEMail = tempEMail;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }
}
