package me.jakob.songreporter.reporting;

import io.github.bonigarcia.wdm.WebDriverManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.jakob.songreporter.GUI.ErrorGUI;
import me.jakob.songreporter.GUI.SummaryGUIController;
import me.jakob.songreporter.Songreporter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Reporter {
    private File script;
    private WebDriver driver;
    private final File errorLog = new File(System.getProperty("user.home") + "/Songreporter/error.log");
    private FileWriter errorWriter;

    public void report(String eMail, String password, String browser, File script, boolean[] categories, ArrayList<Song> songList) throws IOException {
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
        driver.get("https:/olr.ccli.com");

        // login to online me.jakob.songreporter.reporting

        driver.findElement(By.id("EmailAddress")).sendKeys(eMail);
        driver.findElement(By.id("Password")).sendKeys(password);
        driver.findElement(By.id("sign-in")).click();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // reporting the songs out of the list of CCLI songnumbers
        for (Song song : songList) {
            // search for the CCLI songnumber
            try {
                WebElement searchBar = driver.findElement(By.id("SearchTerm"));
                searchBar.clear();
                searchBar.sendKeys(song.getCcliNumber());
                driver.findElement(By.xpath("//*[@id=\"searchBar\"]/div/div/button")).click();
            } catch (NoSuchElementException e) {
                error(e, "Search Bar not found. The code of the website might have changed.\n" +
                        "Please report this to me so I can adapt my code to the changes.");
                break;
            }

            try {
                // Extracting the internal songnumber and expanding the reporting field
                String songNumber = "";
                try {
                    songNumber = driver.findElement(By.className("searchResultsSongSummary")).getAttribute("id");
                } catch (NoSuchElementException e) {
                    throw new NoSearchResultsException(song.getCcliNumber());
                }

                try {
                    driver.findElement(By.xpath("//*[@id=\"" + songNumber + "\"]/div/div[1]/span[1]")).click();
                } catch (NoSuchElementException e) {
                    throw new SongNotReportableException(song);
                }

                songNumber = songNumber.substring(5);

                if (categories[0]) {
                    // increasing print count by 1
                    WebElement printCount = driver.findElement(By.id("PrintCount-" + songNumber));
                    Thread.sleep(750);
                    printCount.click();
                    printCount.clear();
                    printCount.sendKeys("1");
                }

                if (categories[1]) {
                    // increasing digital count by 1
                    WebElement digitalCount = driver.findElement(By.id("DigitalCount-" + songNumber));
                    Thread.sleep(750);
                    digitalCount.click();
                    digitalCount.clear();
                    digitalCount.sendKeys("1");
                }

                if (categories[2]) {
                    // increasing stream count by 1
                    WebElement streamCount = driver.findElement(By.id("RecordingCount-" + songNumber));
                    Thread.sleep(750);
                    streamCount.click();
                    streamCount.clear();
                    streamCount.sendKeys("1");
                }

                if (categories[3]) {
                    // increasing translation count by 1
                    WebElement translationCount = driver.findElement(By.id("TranslationCount-" + songNumber));
                    Thread.sleep(750);
                    translationCount.click();
                    translationCount.clear();
                    translationCount.sendKeys("1");
                }


                // submitting the form and removing the CCLI songnumber
                driver.findElement(By.xpath("//*[@id=\"AddCCL-" + songNumber + "\"]/div[1]/div[5]/button")).click();
                song.markReported();
            } catch (NoSearchResultsException | SongNotReportableException | InterruptedException e) {
                error(e, e.getMessage());
                if (e.getClass() == SongNotReportableException.class) {
                    song.markUnreported("Song not licensed or Website changed");
                } else if (e.getClass() == NoSearchResultsException.class) {
                    song.markUnreported("No search result for this CCLI number");
                }
            }


            // a little delay for an animation on the website
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        errorWriter.close();
        driver.findElement(By.xpath("//*[@id=\"searchResultFooter\"]/div/ul/li[5]/a")).click();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();

        try {
            markAsReported();
        } catch (IOException e) {
            e.printStackTrace();
        }

        summarise(songList);
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
            errorWriter.write("--------------------------------------------------\n\n");
            errorWriter.write("Error while reporting. Error:\n" + e.getMessage() + "\n\n");
            errorWriter.write(message + "\n\n");
            errorWriter.flush();
            e.printStackTrace();
        } catch (IOException e1) {
            new ErrorGUI().showNewErrorMessage("Error logging failed");
        }
    }

    private void summarise(ArrayList<Song> songList) {
        Stage summaryStage = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/summaryGUI.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            error(e, e.getMessage());
        }

        if (root != null) {
            summaryStage.setTitle("Summary");
            summaryStage.setScene(new Scene(root, 920, 263));
            summaryStage.setResizable(true);

            SummaryGUIController summaryGUIController = fxmlLoader.getController();
            try {
                summaryGUIController.summarise(songList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            summaryStage.show();
        }
    }
}
