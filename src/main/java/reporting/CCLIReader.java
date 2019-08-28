package reporting;

import GUI.ErrorGUI;
import config.ConfigManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CCLIReader {

    public ArrayList<String> start(ConfigManager configManager) {
        ArrayList<String> ccliList = new ArrayList<>();
        ArrayList<String> songList = new ArrayList<>();
        File script = new File("");

        // showing GUI to choose a script until the chosen file is one
        while (!(script.getName().endsWith(".col"))) {
            JFileChooser scriptChooser = new JFileChooser();
            scriptChooser.setPreferredSize(new Dimension(900, 700));
            scriptChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            // setting the starting directory of the GUI to the scripts directory in the dropbox if it exists
            File standardScriptsDirectory = new File(configManager.getDropboxPath() + "/SongBeamer/Scripts");
            if (standardScriptsDirectory.exists()) {
                scriptChooser.setCurrentDirectory(standardScriptsDirectory);
            } else {
                scriptChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            }
            scriptChooser.setDialogTitle("Ablaufplan auswählen");
            scriptChooser.showDialog(null, "Auswählen");
            script = scriptChooser.getSelectedFile();
        }

        // read script and extract songs
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(script.getPath()), StandardCharsets.UTF_8));
            StringBuilder songname = new StringBuilder();
            String songsDirectory = configManager.getDropboxPath() + "/SongBeamer/Songs/";
            while (bufferedReader.ready()) {
                String scriptLine = bufferedReader.readLine().trim();
                scriptLine = replaceSpecialCharacters(scriptLine);
                if (scriptLine.contains("FileName") && (scriptLine.contains("+") || scriptLine.endsWith(".sng"))) {
                    songname = new StringBuilder(scriptLine.substring(scriptLine.indexOf("=")+2));
                } else if (scriptLine.contains("FileName")) {
                    songname = new StringBuilder();
                } else if (scriptLine.contains("+")) {
                    songname.append(scriptLine, 0, scriptLine.length()-2);
                } else if (scriptLine.contains("end") && songname.toString().endsWith(".sng")) {
                    songList.add(songname.toString());
                    songname = new StringBuilder();
                } else if (scriptLine.endsWith("g")) {
                    songname.append(scriptLine);
                }
            }

            // going through the song files and reading the ccli songnumbers out of them
            for (String song : songList) {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(songsDirectory + song), StandardCharsets.UTF_8));
                    while (bufferedReader.ready()) {
                        String songLine = bufferedReader.readLine().trim();
                        if (songLine.contains("CCLI")) {
                            ccliList.add(songLine.substring(songLine.indexOf("=") + 1));
                            break;
                        }
                    }
                } catch (IOException e) {
                    new ErrorGUI().showNewErrorMessage(e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ccliList;
    }

    // funtion to replace the special characters of a String out of a script
    // because Sonbeamer can't write these characters into the script or song files
    private String replaceSpecialCharacters(String oldString) {
        System.out.println(oldString + "\n");
        String newString = oldString;
        newString = newString.replace("'", "");
        newString = newString.replace("#228", "ä");
        newString = newString.replace("#252", "ü");
        newString = newString.replace("#246", "ö");
        newString = newString.replace("o#776", "ö");
        newString = newString.replace("#223", "ß");
        newString = newString.replace("#196", "Ä");
        newString = newString.replace("#220", "Ü");
        newString = newString.replace("#214", "Ö");
        newString = newString.replace("#39", "'");
        System.out.println(newString + "\n\n");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return newString;
    }
}
