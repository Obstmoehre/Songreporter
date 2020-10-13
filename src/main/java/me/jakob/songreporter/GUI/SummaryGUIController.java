package me.jakob.songreporter.GUI;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import me.jakob.songreporter.reporting.Song;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SummaryGUIController {

    public ListView summaryList;

    public void summarise(ArrayList<Song> songList) throws FileNotFoundException {
        for (Song song : songList) {
            BorderPane songPane = new BorderPane();
            songPane.setPrefWidth(550);
            songPane.setPrefHeight(50);

            Label titleAndCcli = new Label(song.getName() + " (CCLI: " + song.getCcliNumber() + ")");
            titleAndCcli.setScaleX(1.5);
            titleAndCcli.setScaleY(1.5);

            if (song.isReported()) {
                titleAndCcli.setStyle("-fx-text-fill: #58a832");
            } else {
                titleAndCcli.setStyle("-fx-text-fill: #ff0000");
            }

            songPane.setCenter(titleAndCcli);
            summaryList.getItems().add(songPane);
        }
    }
}
