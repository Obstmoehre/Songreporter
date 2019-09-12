import config.ConfigLoader;
import config.ConfigManager;
import reporting.CCLIReader;
import reporting.Reporter;
import server.Server;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        //starting Server for the GUI
        Server server = Server.getInstance();
        Thread serverThread = new Thread(server);
        serverThread.start();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.stop = true;
        System.out.println("stop");

        // loading config File
        //ConfigManager configManager = new ConfigLoader().load();

        // reading the ccli songnumbers out of the script
        //ArrayList<String> ccliList = new CCLIReader().start(configManager);

        // open browser and report the given ccli songnumbers
        //new Reporter().report(configManager, ccliList);
    }
}
