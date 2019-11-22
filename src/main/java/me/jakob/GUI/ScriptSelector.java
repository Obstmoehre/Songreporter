package me.jakob.GUI;

import me.jakob.config.ConfigManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

class ScriptSelector {

    File selectScript(ConfigManager configmManager) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setPreferredSize(new Dimension(900, 700));
        fileChooser.setDialogTitle("Ablaufplan auswählen");

        File standardScriptsDirectory = new File(configmManager.getDropboxPath() + "/SongBeamer/Scripts");
        if (standardScriptsDirectory.exists()) {
            fileChooser.setCurrentDirectory(standardScriptsDirectory);
        } else {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }

        fileChooser.showDialog(null, "Auswählen");
        return fileChooser.getSelectedFile();
    }
}
