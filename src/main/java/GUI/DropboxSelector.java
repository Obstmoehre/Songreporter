package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DropboxSelector {

    public String selectDropbox() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setPreferredSize(new Dimension(900, 700));
        fileChooser.setDialogTitle("Dropbox Ordner auswählen");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.showDialog(null, "Auswählen");
        File dropbox = fileChooser.getSelectedFile();
        return dropbox.getPath();
    }

}
