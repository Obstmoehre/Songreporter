package me.jakob.songreporter.reporting;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.jakob.songreporter.GUI.controller.SummaryGUIController;
import me.jakob.songreporter.GUI.elements.ErrorGUI;
import me.jakob.songreporter.config.Config;
import me.jakob.songreporter.reporting.enums.Reason;
import me.jakob.songreporter.reporting.exceptions.*;
import me.jakob.songreporter.reporting.objects.Song;
import me.jakob.songreporter.reporting.services.CCLIReadingService;
import me.jakob.songreporter.reporting.services.RESTService;
import me.jakob.songreporter.reporting.services.SeleniumService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Reporter {
    private File script;
    private final File errorLog = new File(System.getProperty("user.home") + "/Songreporter/error.log");
    private FileWriter errorWriter;
    private final CCLIReadingService ccliReadingService;
    private final SeleniumService seleniumService;

    public Reporter() {
        this.ccliReadingService = new CCLIReadingService();
        this.seleniumService = SeleniumService.getInstance();
    }

    public void report(Config config, File script) {
        try {
            errorWriter = new FileWriter(errorLog, true);
        } catch (IOException e) {
            new ErrorGUI().showNewErrorMessage("Failed to open File writer" +
                    "\n" + e.getMessage());
        }
        seleniumService.init(config.getBrowser());

        ArrayList<Song> songs = ccliReadingService.readCcliSongnumbers(config.getSongsDirectory(), script);
        this.script = script;
        boolean loginSuccess;

        try {
            loginSuccess = seleniumService.login(config.getEMail(), config.getPassword());
        } catch (CCLILoginException e) {
            loginSuccess = false;
            if (WrongCredentialsException.class.equals(e.getClass())) {
                for (Song song : songs) {
                    song.markUnreported(Reason.INVALID_CREDENTIALS);
                }
                error(e, "Invalid Credentials");
            } else if (WebsiteChangedException.class.equals(e.getClass())) {
                for (Song song : songs) {
                    song.markUnreported(Reason.SITE_CODE_CHANGED);
                }
                error(e, "Most probably the code of the Website has changed.");
            } else if (TimeoutException.class.equals(e.getClass())) {
                for (Song song : songs) {
                    song.markUnreported(Reason.LOGIN_TIMEOUT);
                }
                error(e, "Timed out while waiting for a page. Please check your");
            } else {
                error(e, "Unknown Error occurred");
            }
        } catch (ServiceNotInitializedException e) {
            seleniumService.init(config.getBrowser());
            loginSuccess = false;
        } catch (InterruptedException e) {
            error(e, "Error while waiting");
            loginSuccess = false;
        }

        HashMap<String, String> cookies = seleniumService.getCookies();
        seleniumService.stop();

        if (loginSuccess) {
            RESTService restService = new RESTService(cookies);
            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);
                if (song.getCcliSongNo() == null) {
                    song.markUnreported(Reason.NO_CCLI_SONGNUMBER);
                } else {
                    song = restService.fetchSongdetails(song.getCcliSongNo());
                    if (song.isPublicDomain()) {
                        song.markUnreported(Reason.SONG_NOT_LICENSED);
                    }
                }

                songs.set(i, song);
            }

            HashMap<Song, Integer> responseCodes = restService.reportSongs(songs, config.getCategories());

            // reporting the songs out of the list of CCLI songnumbers
            for (Song song : songs) {
                switch (responseCodes.get(song)) {
                    case 200:
                        song.markReported();
                        break;
                    case -1:
                        song.markUnreported(Reason.FAILED_REQUEST);
                        break;
                    case -2:
                        song.markUnreported(Reason.NO_RESPONSE_BODY);
                        break;
                    case -3:
                        song.markUnreported(Reason.NO_REQUEST_VERIFICATION_TOKEN);
                        break;
                    default:
                        song.markUnreported(Reason.ERRORCODE);
                        break;
                }
            }
            try {
                errorWriter.close();
            } catch (IOException e) {
                new ErrorGUI().showNewErrorMessage("Failed to open File writer" +
                        "\n" + e.getMessage());
            }

            try {
                markAsReported();
            } catch (IOException e) {
                error(e, "Failed to mark the flowsheet as reported");
            }
        }

        summarise(songs);
    }

    private void markAsReported() throws IOException {
        FileWriter fileWriter = new FileWriter(this.script, true);
        fileWriter.append("#reported");
        fileWriter.flush();
        fileWriter.close();
    }

    private void error(Exception e, String message) {
        try {
            if (!errorLog.exists()) {
                if (!(errorLog.createNewFile())) {
                    new ErrorGUI().showNewErrorMessage("Failed to create new error log.");
                }
            }

            String seperator = "------------------------------------------";
            errorWriter.write(seperator + new Date().toString() + seperator + "\n\n");
            errorWriter.write("Error while reporting. Error:\n" + e.getMessage() + "\n\n");
            if (!message.equals("")) {
                errorWriter.write(message + "\n\n");
            }
            errorWriter.flush();
            e.printStackTrace();
        } catch (IOException e1) {
            new ErrorGUI().showNewErrorMessage("Error logging failed");
        }
    }

    private void summarise(ArrayList<Song> songList) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/summaryGUI.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            error(e, "");
        }

        if (root != null) {
            Stage summaryStage = new Stage();
            summaryStage.setTitle("Summary");
            summaryStage.setScene(new Scene(root, 600, 400));
            summaryStage.setResizable(false);

            SummaryGUIController summaryGUIController = fxmlLoader.getController();
            summaryGUIController.summarise(songList);

            summaryStage.show();
        }
    }
}
