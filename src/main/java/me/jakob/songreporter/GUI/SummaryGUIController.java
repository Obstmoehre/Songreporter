package me.jakob.songreporter.GUI;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import me.jakob.songreporter.reporting.Song;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SummaryGUIController {

    public Pane summaryPane;

    public void summarise(ArrayList<Song> songList) throws FileNotFoundException {
        for (Song song : songList) {
            BorderPane songPane = new BorderPane();
            songPane.setPrefWidth(600);
            songPane.setPrefHeight(50);

            Label titleAndCcli = new Label(song.getName() + " (" + song.getCcliNumber() + ")");

            Image checkMark;
            if (song.isReported()) {
                checkMark = new Image(new FileInputStream("src/main/resources/assets/checkmark2.png"));
            } else {
                checkMark = new Image(new FileInputStream("src/main/resources/assets/cross3.png"));
            }
            ImageView checkMarkView = new ImageView(checkMark);

            songPane.setLeft(titleAndCcli);
            songPane.setRight(checkMarkView);
            summaryPane.getChildren().add(songPane);
        }
    }
}
