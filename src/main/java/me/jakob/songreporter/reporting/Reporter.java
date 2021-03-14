package me.jakob.songreporter.reporting;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.jakob.songreporter.GUI.controller.SummaryGUIController;
import me.jakob.songreporter.GUI.elements.ErrorGUI;
import me.jakob.songreporter.reporting.enums.Reason;
import me.jakob.songreporter.reporting.exceptions.*;
import me.jakob.songreporter.reporting.objects.Song;
import me.jakob.songreporter.reporting.services.CCLIReadingService;
import me.jakob.songreporter.reporting.services.RESTService;
import me.jakob.songreporter.reporting.services.SeleniumService;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Reporter {
    private File script;
    private WebDriver driver;
    private final File errorLog = new File(System.getProperty("user.home") + "/Songreporter/error.log");
    private FileWriter errorWriter;
    private boolean websiteChangeFlag;
    private final CCLIReadingService ccliReadingService;
    private final SeleniumService seleniumService;
    private RESTService restService;
    private final boolean testMode;

    public Reporter(boolean testMode) {
        this.testMode = testMode;
        this.seleniumService = SeleniumService.getInstance();
        this.ccliReadingService = new CCLIReadingService();
    }

    public void report(String eMail, String password, String browser, File script, 
                       boolean[] categories, ArrayList<String> ccliSongNumbers) throws IOException {
        ArrayList<Song> songs = new ArrayList<>();
        errorWriter = new FileWriter(errorLog, true);
        this.script = script;
        seleniumService.init(browser);
        boolean loginSuccess;

        try {
            loginSuccess = seleniumService.login(eMail, password);
        } catch (CCLILoginException e) {
            for (String ccliSongnumber : ccliSongNumbers) {
                songs.add(new Song(ccliSongnumber));
            }
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
            error(e, "Selenium Service not initialized");
            loginSuccess = false;
        } catch (InterruptedException e) {
            error(e, "Error while waiting");
            loginSuccess = false;
        }

        if (loginSuccess) {
            this.restService = new RESTService(seleniumService.getCookies());
            for (String ccliSongNumber : ccliSongNumbers) {
                Song song = restService.fetchSongdetails(ccliSongNumber);
                if (song.isPublicDomain()) {
                    song.markUnreported(Reason.SONG_NOT_LICENSED);
                } else {
                    song.markUnreported(Reason.NO_CCLI_SONGNUMBER);
                }
                songs.add(song);
            }

            HashMap<Song, Integer> responseCodes = restService.reportSongs(songs);

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
            errorWriter.close();
            driver.quit();

            try {
                markAsReported();
            } catch (IOException e) {
                error(e, "Failed to mark the flowsheet as reported");
            }
        }

        summarise(songs, websiteChangeFlag);
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

    private void summarise(ArrayList<Song> songList, boolean websiteChangeFlag) {
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
            summaryGUIController.summarise(songList, websiteChangeFlag);

            summaryStage.show();
        }
    }

    private void waitForLoadingScreen() {
        int tries = 0;
        int maxTries = 300;

        while (tries < maxTries) {
            try {
                driver.findElement(By.xpath("//*[@id=\"page-loading-overlay\"]"));
                tries = maxTries;
            } catch (NoSuchElementException e) {
                tries++;
                if (tries >= maxTries) {
                    error(e, "timeout while waiting for loading screen");
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException interruptedException) {
                    error(interruptedException, "sleep command failed");
                }
            }
        }

        try {
            while (driver.findElement(By.xpath("//*[@id=\"page-loading-overlay\"]"))
                    .getAttribute("aria-busy").equals("true")) {
                //noinspection BusyWait
                Thread.sleep(50);
            }
        } catch (NoSuchElementException e) {
            error(e, "loading screen not found");
        } catch (InterruptedException e) {
            error(e, "sleep command failed");
        }
    }

    private boolean checkLogin() {
        try {
            return !driver.findElement(By.xpath("//*[@id=\"sign-into-account\"]/div[1]/div/div/p[2]"))
                    .getText().contains("Die E-Mailadresse oder das Passwort wurden nicht gefunden.");
        } catch (NoSuchElementException e) {
            websiteChangeFlag = true;
            return true;
        } catch (StaleElementReferenceException e) {
            return true;
        }
    }
}
