import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // loading config File
        ConfigManager configManager = new ConfigLoader().load();

        // reading the ccli songnumbers out of the script
        ArrayList<String> ccliList = new CCLIReader().start(configManager);

        // open browser and report the given ccli songnumbers
        new Reporter().report(configManager, ccliList);
    }
}
