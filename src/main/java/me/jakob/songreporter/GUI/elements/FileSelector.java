package me.jakob.songreporter.GUI.elements;

import javafx.stage.FileChooser;

import java.io.File;

public class FileSelector {

    public FileSelector(String title, String currentDirectory) {
        this.title = title;
        this.currentDirectory = currentDirectory;
    }

    String title;
    String currentDirectory;

    public File select() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        if (this.currentDirectory != null && new File(this.currentDirectory).exists()) {
            fileChooser.setInitialDirectory(new File(this.currentDirectory));
        }

        return fileChooser.showOpenDialog(null);
    }
}
