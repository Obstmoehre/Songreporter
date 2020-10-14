package me.jakob.songreporter.GUI;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import me.jakob.songreporter.reporting.Song;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SummaryGUIController {

    public ListView summaryList;

    public void summarise(ArrayList<Song> songList) throws FileNotFoundException {
        for (Song song : songList) {
            VBox songBox = new VBox();

            BorderPane titlePane = new BorderPane();
            titlePane.setPrefWidth(550);
            titlePane.setPrefHeight(40);

            BorderPane reasonPane = new BorderPane();
            reasonPane.setPrefWidth(550);
            reasonPane.setPrefHeight(10);
            Label titleAndCcli;
            Label reasonLabel = null;

            if (song.getCcliNumber() == null) {
                titleAndCcli = new Label(song.getName() + " (CCLI: No CCLI Number found)");
            } else {
                titleAndCcli = new Label(song.getName() + " (CCLI: " + song.getCcliNumber() + ")");
            }

            titleAndCcli.setScaleX(1.5);
            titleAndCcli.setScaleY(1.5);

            if (song.isReported()) {
                titleAndCcli.setStyle("-fx-text-fill: #58a832");
            } else {
                titleAndCcli.setStyle("-fx-text-fill: #ff0000");
                reasonLabel = new Label(song.getReason());
                reasonPane.setCenter(reasonLabel);
            }

            titlePane.setCenter(titleAndCcli);

            songBox.getChildren().add(titlePane);
            if (reasonLabel != null) {
                songBox.getChildren().add(reasonPane);
            }

            summaryList.getItems().add(songBox);
        }
    }
}
