package me.jakob.GUI;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DriverSelector {

    public String selectDriver() {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setPreferredSize(new Dimension((int) screen.getWidth(), (int) screen.getHeight()));
        fileChooser.setDialogTitle("Browser Treiber auswählen");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.showDialog(null, "Auswählen");
        File driver = fileChooser.getSelectedFile();
        if (driver == null) {
            return "";
        } else {
            return driver.getPath();
        }
    }

}
