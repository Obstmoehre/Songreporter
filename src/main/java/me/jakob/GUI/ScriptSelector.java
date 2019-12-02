package me.jakob.GUI;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import me.jakob.config.ConfigManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

class ScriptSelector {

    File selectScript(ConfigManager configmManager) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setPreferredSize(new Dimension((int) screen.getWidth(), (int) screen.getHeight()));
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
