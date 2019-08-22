package config;

import com.google.gson.Gson;

import java.io.*;

public class ConfigManager {
    // variables for saving the path to the dropbox directory and the browserdriver
    private String driverPath;
    private String dropboxPath;

    // function to save the values into the config file
    void saveConfig(Gson gson, File config) {
        String toJson = gson.toJson(this);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config)));
            bufferedWriter.write(toJson);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDriverPath() {
        return this.driverPath;
    }

    public String getDropboxPath() {
        return this.dropboxPath;
    }

    void setDriverPath(String driverPath) {
        this.driverPath = driverPath;
    }

    void setDropboxPath(String dropboxPath) {
        this.dropboxPath = dropboxPath;
    }
}


