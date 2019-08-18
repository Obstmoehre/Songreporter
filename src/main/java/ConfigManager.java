import com.google.gson.Gson;

import java.io.*;

class ConfigManager {
    private String driverPath;
    private String dropboxPath;

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

    String getDriverPath() {
        return this.driverPath;
    }

    String getDropboxPath() {
        return this.dropboxPath;
    }

    void setDriverPath(String driverPath) {
        this.driverPath = driverPath;
    }

    void setDropboxPath(String dropboxPath) {
        this.dropboxPath = dropboxPath;
    }
}


