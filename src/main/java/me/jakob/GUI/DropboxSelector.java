package me.jakob.GUI;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DropboxSelector {

    public String selectDropbox() {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setPreferredSize(new Dimension((int) screen.getWidth(), (int) screen.getHeight()));
        fileChooser.setDialogTitle("Dropbox Ordner auswählen");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.showDialog(null, "Auswählen");
        File dropbox = fileChooser.getSelectedFile();
        if (dropbox == null) {
            return "";
        } else {
            return dropbox.getPath();
        }
    }

}
