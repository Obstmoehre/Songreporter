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
            Label titleLabel;
            Label reasonLabel = null;

            if (song.getCcliNumber() == null) {
                titleLabel = new Label(song.getName() + " (CCLI: No CCLI number found)");
            } else {
                titleLabel = new Label(song.getName() + " (CCLI: " + song.getCcliNumber() + ")");
            }

            titleLabel.setScaleX(1.5);
            titleLabel.setScaleY(1.5);

            if (song.isReported()) {
                titleLabel.setStyle("-fx-text-fill: #58a832");
            } else {
                switch (song.getReason()) {
                    case NO_SEARCH_RESULTS: {
                        reasonLabel = new Label("No search results found for this CCLI songnumber" +
                                " [click for details");
                        break;
                    }
                    case SONG_NOT_LICENSED: {
                        reasonLabel = new Label("The song is under no license [click for details]");
                        break;
                    }
                    case NO_CCLI_SONGNUMBER: {
                        reasonLabel = new Label("No CCLI songnumber found in the songfile [click for details]");
                        break;
                    }
                    case SITE_CODE_CHANGED: {
                        reasonLabel = new Label("The code of the website has changed [click for details]");
                        break;
                    }
                    default: {
                        reasonLabel = new Label("Unknown reason");
                    }
                }
                titleLabel.setStyle("-fx-text-fill: #ff0000");
                reasonPane.setCenter(reasonLabel);
            }

            titlePane.setCenter(titleLabel);

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
                if (reasonLabel.getText().contains("no license")) {
                    alert.setContentText(reasonLabel.getText().replace("[click for details]", "") +
                            "\nOnly possible when no song was reported successfully. If so please check if any song" +
                            " is licensed and report the licensed songs manually.");
                } else if (reasonLabel.getText().contains("No search results")) {
                    alert.setContentText(reasonLabel.getText().replace("[click for details]", "") +
                            "\nPlease check this manually. It is likely that" +
                            " the code of the website has changed and therefore the program can't find the result.");
                } else if (reasonLabel.getText().contains("No CCLI songnumber")) {
                    alert.setContentText(reasonLabel.getText().replace("[click for details]", "") +
                            "\nPlease check if a CCLI Songnumber is availabe " +
                            " for the song and insert it.");
                } else if (reasonLabel.getText().contains("code")) {
                    alert.setContentText(reasonLabel.getText().replace("[click for details]", "") +
                            "\nPlease report this to me.");
                }

                alert.showAndWait();
            }
        });
    }
}
