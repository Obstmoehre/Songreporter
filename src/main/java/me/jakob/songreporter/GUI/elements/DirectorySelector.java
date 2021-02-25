package me.jakob.songreporter.GUI.elements;

import javafx.stage.DirectoryChooser;

import java.io.File;

public class DirectorySelector {

    public DirectorySelector(String title) {
        this.title = title;
        this.currentDirectory = null;
    }

    String title;
    String currentDirectory;

    public String select() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);

        if (this.currentDirectory != null && new File(this.currentDirectory).exists()) {
            directoryChooser.setInitialDirectory(new File(this.currentDirectory));
        }

        File selectedFile = directoryChooser.showDialog(null);

        if (selectedFile != null) {
            return selectedFile.getPath() + "\\";
        } else {
            return null;
        }
    }
}
