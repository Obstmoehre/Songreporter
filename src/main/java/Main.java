import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader();
        ConfigManager configManager = configLoader.load();

        ArrayList<String> ccliList = new CCLIReader().start(configManager);

        new Reporter().report(configManager, ccliList);
    }
}
