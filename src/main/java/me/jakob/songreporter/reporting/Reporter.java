package me.jakob.songreporter.reporting;

import io.github.bonigarcia.wdm.WebDriverManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.jakob.songreporter.GUI.elements.ErrorGUI;
import me.jakob.songreporter.GUI.controller.SummaryGUIController;
import me.jakob.songreporter.reporting.enums.Reason;
import me.jakob.songreporter.reporting.enums.WebsiteElement;
import me.jakob.songreporter.reporting.exceptions.CategoryNotReportableException;
import me.jakob.songreporter.reporting.exceptions.SongNotLicencedException;
import me.jakob.songreporter.reporting.exceptions.WebsiteChangedException;
import me.jakob.songreporter.reporting.exceptions.WrongCredentialsException;
import me.jakob.songreporter.reporting.objects.Song;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class Reporter {
    private File script;
    private WebDriver driver;
    private final File errorLog = new File(System.getProperty("user.home") + "/Songreporter/error.log");
    private FileWriter errorWriter;
    private boolean websiteChangeFlag;

    public void report(String eMail, String password, String browser, File script, boolean[] categories,
                       ArrayList<Song> songList) throws IOException {
        errorWriter = new FileWriter(errorLog, true);
        this.script = script;

        // starting driver and going to main page
        switch (browser) {
            case "Chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "Firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "Opera":
                WebDriverManager.operadriver().setup();
                driver = new OperaDriver();
                break;
        }

        int retries = 0;
        int maxRetries = 3;

        while (retries < maxRetries) {
            driver.get("https://olr.ccli.com");

            // waiting for login page
            boolean isloaded = false;
            int triesBeforeTimeout = 0;
            while (!isloaded && triesBeforeTimeout < 30) {
                try {
                    driver.findElement(By.id("EmailAddress"));
                    isloaded = true;
                } catch (NoSuchElementException e) {
                    triesBeforeTimeout++;
                    if (triesBeforeTimeout >= 30) {
                        error(e, "Timed out while searching for the E-Mail Field");
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException interruptedException) {
                        error(interruptedException, "sleep command failed");
                    }
                }
            }

            // login to online reporting
            try {
                try {
                    driver.findElement(By.id("EmailAddress")).sendKeys(eMail);
                } catch (NoSuchElementException e) {
                    throw new WebsiteChangedException(WebsiteElement.E_MAIL_FIELD);
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    error(e, "sleep command failed");
                }

                try {
                    driver.findElement(By.id("Password")).sendKeys(password);
                } catch (NoSuchElementException e) {
                    throw new WebsiteChangedException(WebsiteElement.PASSWORD_FIELD);
                }

                try {
                    driver.findElement(By.id("sign-in")).click();
                } catch (NoSuchElementException e) {
                    throw new WebsiteChangedException(WebsiteElement.LOGIN_BUTTON);
                }
                retries = maxRetries;
            } catch (WebsiteChangedException e) {
                retries++;
                if (retries >= maxRetries) {
                    error(e, "Changed Element: " + e.getChangedElement());
                    for (Song song : songList) {
                        song.markUnreported(Reason.SITE_CODE_CHANGED);
                    }
                }
            }
        }

        boolean loginSuccess;

        try {
            initialWaitForLoadingScreen();
            loginSuccess = true;
        } catch (WrongCredentialsException e) {
            error(e, "");
            loginSuccess = false;
            for (Song song : songList) {
                song.markUnreported(Reason.INVALID_CREDENTIALS);
            }
        } catch (WebsiteChangedException e) {
            error(e, "Changed Element: " + e.getChangedElement());
            loginSuccess = false;
            for (Song song : songList) {
                song.markUnreported(Reason.SITE_CODE_CHANGED);
            }
        }

        if (loginSuccess) {
            waitForLoadingScreen();

            boolean init = true;
            // reporting the songs out of the list of CCLI songnumbers
            boolean anySuccess = false;
            for (Song song : songList) {
                try {
                    if (song.getCcliNumber() != null) {
                        // search for the CCLI songnumber

                        try {
                            WebElement searchBar = driver.findElement(By.id("SearchIinput"));
                            searchBar.clear();
                            searchBar.sendKeys(song.getCcliNumber());
                        } catch (NoSuchElementException e) {
                            throw new WebsiteChangedException(WebsiteElement.SEARCH_BAR);
                        }
                        if (init) {
                            try {
                                driver.findElement(By.xpath("//*[@id=\"MainWrapper\"]/div/div[1]/div/main/div[1]/" +
                                        "div[2]/div/div/div[2]/div/button[2]")).click();
                                init = false;
                            } catch (NoSuchElementException e) {
                                throw new WebsiteChangedException(WebsiteElement.SEARCH_BUTTON_INIT);
                            }
                        } else {
                            try {
                                driver.findElement(By.xpath("//*[@id=\"MainWrapper\"]/div/div[1]/div/main/div[1]/" +
                                        "div[2]/div[1]/div/div[2]/div/button[2]")).click();
                            } catch (NoSuchElementException e) {
                                throw new WebsiteChangedException(WebsiteElement.SEARCH_BUTTON_SECONDARY);
                            }
                        }

                        waitForLoadingScreen();

                        // Opening the Report-From
                        try {
                            driver.findElement(By.xpath("//*[@id=\"SearchResultsAlbums\"]/div[2]/div/div/div/div/table/" +
                                    "tbody[1]/tr/td[7]/button")).click();
                        } catch (NoSuchElementException e) {
                            throw new WebsiteChangedException(WebsiteElement.REPORT_BUTTON);
                        }

                        waitForLoadingScreen();

                        try {
                            for (int i = 0; i < categories.length; i++) {
                                if (categories[i]) {
                                    anySuccess = setCategory(i);
                                }
                            }
                        } catch (CategoryNotReportableException e) {
                            if ((!anySuccess) || checkForLicence(categories, e.getFailedCategory())) {
                                throw new WebsiteChangedException(WebsiteElement.CATEGORIES);
                            } else {
                                driver.findElement(By.xpath("//*[@id=\"ModalReportSongForm\"]/div[3]/button")).click();
                                throw new SongNotLicencedException(song);
                            }
                        }

                        // submitting the form

                        // testing command (just closing form instead of saving)
//                        driver.findElement(By.xpath("//*[@id=\"ModalReportSongModal\"]/button/span")).click();

                        // final command (actually saving the inputs made)
                        try {
                            driver.findElement(By.xpath("//*[@id=\"ModalReportSongForm\"]/div[3]/button[2]")).click();
                        } catch (NoSuchElementException e) {
                            throw new WebsiteChangedException(WebsiteElement.SAVE_BUTTON);
                        }

                        song.markReported();
                    } else {
                        song.markUnreported(Reason.NO_CCLI_SONGNUMBER);
                    }
                } catch (WebsiteChangedException | SongNotLicencedException e) {
                    if (e.getClass() == WebsiteChangedException.class) {
                        error(e, "Changed Element: " + ((WebsiteChangedException) e).getChangedElement());
                        for (Song song1 : songList) {
                            song1.markUnreported(Reason.SITE_CODE_CHANGED);
                        }
                        break;
                    } else if (e.getClass() == SongNotLicencedException.class) {
                        song.markUnreported(Reason.SONG_NOT_LICENSED);
                    }
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

        summarise(songList, websiteChangeFlag);
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

    private boolean checkForLicence(boolean[] categories, int failedCategory) {
        try {
            return !driver.findElement(By.xpath("//*[@id=\"ModalReportSongForm\"]/div[2]/div[1]/" +
                    "div/div/div[2]/div[1]/div/div[2]")).getText().contains("Public Domain");
        } catch (NoSuchElementException e) {
            websiteChangeFlag = true;
            try {
                driver.findElement(By.xpath("//*[@id=\"ModalReportSongForm\"]/div[3]/button[2]"));
                return true;
            } catch (NoSuchElementException ex) {
                boolean licence = true;
                for (int i = 0; i < 4; i++) {
                    if ((i < failedCategory && !categories[i]) || i > failedCategory) {
                        try {
                            if (!setCategory(i)) {
                                return true;
                            }
                        } catch (CategoryNotReportableException exc) {
                            licence = false;
                        }
                    }
                }
                return licence;
            }
        }
    }

    private boolean setCategory(int category) throws CategoryNotReportableException {
        try {
            switch (category) {
                case 0: {
                    // increasing print count by 1
                    Select printCount = new Select(driver.findElement(By.id("cclPrint")));
                    printCount.selectByVisibleText("1");
                    return true;
                }
                case 1: {
                    // increasing digital count by 1
                    Select digitalCount = new Select(driver.findElement(By.id("cclDigital")));
                    digitalCount.selectByVisibleText("1");
                    return true;
                }
                case 2: {
                    // increasing stream count by 1
                    Select streamCount = new Select(driver.findElement(By.id("cclRecord")));
                    streamCount.selectByVisibleText("1");
                    return true;
                }
                case 3: {
                    // increasing translation count by 1
                    Select translationCount = new Select(driver.findElement(By.id("cclTranslate")));
                    translationCount.selectByVisibleText("1");
                    return true;
                }
            }
        } catch (NoSuchElementException e) {
            throw new CategoryNotReportableException(category);
        }
        return false;
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

    private void initialWaitForLoadingScreen() throws WrongCredentialsException, WebsiteChangedException {
        int tries = 0;
        int maxTries = 300;

        while (tries < maxTries) {
            try {
                driver.findElement(By.xpath("//*[@id=\"page-loading-overlay\"]"));
                tries = maxTries;
            } catch (NoSuchElementException e) {
                tries++;
                if (tries >= maxTries) {
                    if (driver.getCurrentUrl().contains("reporting.ccli.com")) {
                        websiteChangeFlag = false;
                        throw new WebsiteChangedException(WebsiteElement.LOADING_SCREEN);
                    } else {
                        throw new WrongCredentialsException();
                    }
                } else if (!checkLogin()) {
                    throw new WrongCredentialsException();
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
