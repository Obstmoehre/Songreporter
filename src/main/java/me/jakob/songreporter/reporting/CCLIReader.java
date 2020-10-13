package me.jakob.songreporter.reporting;

import me.jakob.songreporter.GUI.ErrorGUI;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CCLIReader {

    public ArrayList<Song> read(String songsDirectory, File script) {
        ArrayList<Song> songList = new ArrayList<>();

        // read script and extract songs
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(script.getPath()), StandardCharsets.UTF_8));
            StringBuilder songName = new StringBuilder();
            while (bufferedReader.ready()) {
                String scriptLine = bufferedReader.readLine().trim();
                scriptLine = replaceSpecialCharacters(scriptLine);
                if (scriptLine.contains("FileName") && (scriptLine.contains("+") || scriptLine.endsWith(".sng"))) {
                    songName = new StringBuilder(scriptLine.substring(scriptLine.indexOf("=")+2));
                } else if (scriptLine.contains("FileName")) {
                    songName = new StringBuilder();
                } else if (scriptLine.contains("+") && !(songName.toString().endsWith(".sng"))) {
                    songName.append(scriptLine, 0, scriptLine.length()-2);
                } else if (scriptLine.contains("end") && songName.toString().endsWith(".sng")) {
                    songList.add(new Song(songName.substring(0, songName.length()-4)));
                    songName = new StringBuilder();
                } else if (scriptLine.endsWith("g")) {
                    songName.append(scriptLine);
                }
            }

            // going through the song files and reading the ccli songnumbers out of them
            for (Song song : songList) {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(songsDirectory + song.getName() + ".sng"), StandardCharsets.UTF_8));
                    while (bufferedReader.ready()) {
                        String songLine = bufferedReader.readLine().trim();
                        if (songLine.contains("CCLI")) {
                            song.setCcliNumber(songLine.substring(songLine.indexOf("=") + 1));
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

        return songList;
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
