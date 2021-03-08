package me.jakob.songreporter.reporting;

import me.jakob.songreporter.GUI.elements.ErrorGUI;
import me.jakob.songreporter.reporting.objects.Song;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CCLIReader {

    public ArrayList<String> read(String songsDirectory, File script) {
        ArrayList<String> ccliSongNumbers = new ArrayList<>();

        // read script and extract songs
        try {
            ArrayList<String> songNameList = new ArrayList<>();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(script.getPath()), StandardCharsets.UTF_8));
            StringBuilder songNameBuilder = new StringBuilder();
            while (bufferedReader.ready()) {
                String scriptLine = bufferedReader.readLine().trim();
                scriptLine = replaceSpecialCharacters(scriptLine);
                if (scriptLine.contains("FileName") && (scriptLine.contains("+") || scriptLine.endsWith(".sng"))) {
                    songNameBuilder = new StringBuilder(scriptLine.substring(scriptLine.indexOf("=")+2));
                } else if (scriptLine.contains("FileName")) {
                    songNameBuilder = new StringBuilder();
                } else if (scriptLine.contains("+") && !(songNameBuilder.toString().endsWith(".sng"))) {
                    songNameBuilder.append(scriptLine, 0, scriptLine.length()-2);
                } else if (scriptLine.contains("end") && songNameBuilder.toString().endsWith(".sng")) {
                    songNameList.add(songNameBuilder.toString());
                    songNameBuilder = new StringBuilder();
                } else if (scriptLine.endsWith("g")) {
                    songNameBuilder.append(scriptLine);
                }
            }

            // going through the song files and reading the ccli songnumbers out of them
            for (String songName : songNameList) {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(
                            songsDirectory + songName), StandardCharsets.UTF_8)
                    );
                    while (bufferedReader.ready()) {
                        String songLine = bufferedReader.readLine().trim();
                        if (songLine.contains("CCLI")) {
                            ccliSongNumbers.add(songLine.substring(songLine.indexOf("=") + 1));
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

        return ccliSongNumbers;
    }

    private String replaceSpecialCharacters(String oldString) {
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
        return newString;
    }
}
