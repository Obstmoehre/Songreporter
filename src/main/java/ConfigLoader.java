import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File config = new File(System.getProperty("user.home") + "/Songreporter/config.json");

    //Configmanager to save config and access the values
    ConfigManager configManager;

    public ConfigManager load() {
        //checking and refreshing config file
        try {
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //getting or if needed setting the path to the browserdriver
        String driverPath = configManager.getDriverPath();
        String dropboxPath = configManager.getDropboxPath();
        File testDriverFile;
        File testDropboxFile;
        if (driverPath.isEmpty()) {
            driverPath = new DriverSelector().selectDriver();
            configManager.setDriverPath(driverPath);
            configManager.saveConfig(gson, config);
            testDriverFile = new File(driverPath);
        } else {
            testDriverFile = new File(driverPath);
        }
        if (!(testDriverFile.exists())) {
            driverPath = new DriverSelector().selectDriver();
            configManager.setDriverPath(driverPath);
            configManager.saveConfig(gson, config);
        }

        if (dropboxPath.isEmpty()) {
            dropboxPath = new DropboxSelector().selectDropbox();
            configManager.setDropboxPath(dropboxPath);
            configManager.saveConfig(gson, config);
            testDropboxFile = new File(dropboxPath);
        } else {
            testDropboxFile = new File(dropboxPath);
        }
        if (!(testDropboxFile.exists())) {
            dropboxPath = new DropboxSelector().selectDropbox();
            configManager.setDriverPath(dropboxPath);
            configManager.saveConfig(gson, config);
        }

        return configManager;
    }

    private void loadConfig() throws IOException {
        //testing if the config.json File exists and creating a new one if not
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

            configManager = gson.fromJson(fromJsonBuilder.toString().trim(), ConfigManager.class);
        }

        if (!isCreated) {
            ErrorGUI jsonErrorGUI = new ErrorGUI();
            jsonErrorGUI.showNewErrorMessage("Es ist ein Fehler mit der json Datei aufgetreten!");
        }
    }

    private void newConfig() {
        configManager = new ConfigManager();

        String driverPath = new DriverSelector().selectDriver();
        configManager.setDriverPath(driverPath);

        String dropboxPath = new DropboxSelector().selectDropbox();
        configManager.setDropboxPath(dropboxPath);

        configManager.saveConfig(gson, config);
    }
}
