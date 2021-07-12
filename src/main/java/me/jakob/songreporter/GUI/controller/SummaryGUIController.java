package me.jakob.songreporter.GUI.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import me.jakob.songreporter.reporting.objects.Song;

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

            if (song.getCcliSongNo() == null) {
                titleLabel = new Label(song.getTitle() + " (CCLI: No CCLI number found)");
            } else {
                titleLabel = new Label(song.getTitle() + " (CCLI: " + song.getCcliSongNo() + ")");
            }

            titleLabel.setScaleX(1.5);
            titleLabel.setScaleY(1.5);

            if (song.isReported()) {
                titleLabel.setStyle("-fx-text-fill: #58a832");
            } else {
                switch (song.getReason()) {
                    case INVALID_CREDENTIALS: {
                        reasonLabel = new Label("You entered invalid Credentials");
                        titleLabel.setStyle("-fx-text-fill: #ff0000");
                        break;
                    }
                    case SONG_NOT_LICENSED: {
                        reasonLabel = new Label("The song is under no license");
                        titleLabel.setStyle("-fx-text-fill: #ff7700");
                        break;
                    }
                    case NO_CCLI_SONGNUMBER: {
                        reasonLabel = new Label("No CCLI songnumber found in the songfile [click for details]");
                        titleLabel.setStyle("-fx-text-fill: #ff0000");
                        break;
                    }
                    case SITE_CODE_CHANGED: {
                        reasonLabel = new Label("The code of the website has changed [click for details]");
                        titleLabel.setStyle("-fx-text-fill: #ff0000");
                        break;
                    }
                    case ERRORCODE:
                    case FAILED_REQUEST:
                    case NO_RESPONSE_BODY:
                    case NO_REQUEST_VERIFICATION_TOKEN: {
                        reasonLabel = new Label("An internal HTTP Error occurred");
                        titleLabel.setStyle("-fx-text-fill: #ff0000");
                        break;
                    }
                    default: {
                        reasonLabel = new Label("Unknown reason");
                    }
                }
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
                if (reasonLabel.getText().contains("invalid")) {
                    alert.setContentText(reasonLabel.getText().replace(" [click for details]", ".") +
                            "\nPlease check your E-Mail and Password."
                    );
                    alert.showAndWait();
                } else if (reasonLabel.getText().contains("No CCLI songnumber")) {
                    alert.setContentText(reasonLabel.getText().replace(" [click for details]", ".") +
                            "\nPlease check if a CCLI Songnumber is availabe" +
                            " for the song and insert it."
                    );
                    alert.showAndWait();
                } else if (reasonLabel.getText().contains("code")) {
                    alert.setContentText(reasonLabel.getText().replace(" [click for details]", ".") +
                            "\nThis is only possible when no song was reported successfully. If this is the case" +
                            " please check if any song is licensed and report the licensed songs manually."
                    );
                    alert.showAndWait();
                }
            }
        });
    }
}
