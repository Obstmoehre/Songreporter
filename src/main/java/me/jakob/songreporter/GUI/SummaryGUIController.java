package me.jakob.songreporter.GUI;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import me.jakob.songreporter.reporting.Song;

import java.util.ArrayList;

public class SummaryGUIController {

    public ListView<VBox> summaryList;

    public void summarise(ArrayList<Song> songList) {
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
                reasonLabel = new Label(song.getReason() + " [click for details]");
                reasonPane.setCenter(reasonLabel);
            }

            titlePane.setCenter(titleAndCcli);

            songBox.getChildren().add(titlePane);
            if (reasonLabel != null) {
                songBox.getChildren().add(reasonPane);
            }

            summaryList.getItems().add(songBox);
        }

        summaryList.setOnMouseClicked(event -> {
            if (summaryList.getSelectionModel().getSelectedItem()
                    .getChildren().size() > 1) {
                BorderPane reasonPane = (BorderPane) summaryList.getSelectionModel().getSelectedItem()
                        .getChildren().get(1);

                Label reasonLabel = (Label) reasonPane.getCenter();

                BorderPane titlePane = (BorderPane) summaryList.getSelectionModel().getSelectedItem()
                        .getChildren().get(0);

                Label titleLabel = (Label) titlePane.getCenter();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Reason");

                alert.setHeaderText(titleLabel.getText());
                if (reasonLabel.getText().equals("Song not licensed or Website changed [click for details]")) {
                    alert.setContentText(reasonLabel.getText() + "\n(Only possible when no song was reported" +
                            " successfully. If so please check if any song is licensed and report the licensed songs" +
                            " manually.)");
                } else if (reasonLabel.getText().equals("No search result for this CCLI number [click for details]")) {
                    alert.setContentText(reasonLabel.getText() + "\n(Please check this manually. It is likely that" +
                            " the code of the website has changed and therefore the program can't find the result.)");
                } else if (reasonLabel.getText().equals("No CCLI Songnumber [click for details]")) {
                    alert.setContentText(reasonLabel.getText() + "\n(Please check if a CCLI Songnumber is availabe " +
                            " for the song and insert it.)");
                }

                alert.showAndWait();
            }
        });
    }
}
