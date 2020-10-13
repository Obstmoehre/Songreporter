package me.jakob.songreporter.reporting;

import io.github.bonigarcia.wdm.WebDriverManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.jakob.songreporter.GUI.ErrorGUI;
import me.jakob.songreporter.GUI.SummaryGUIController;
import me.jakob.songreporter.test.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.Select;

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

        // login to online reporting

        boolean isloaded = false;
        int tries = 0;

        while (!isloaded && tries < 10) {
            try {
                driver.findElement(By.id("EmailAddress"));
                isloaded = true;
            } catch (NoSuchElementException e) {
                tries++;
                if (tries >= 10) {
                    error(e, "E-Mail Field not found. The code of the website might have changed.\n" +
                            "Please report this to me so I can adapt my code to the changes.");
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

        driver.findElement(By.id("EmailAddress")).sendKeys(eMail);
        driver.findElement(By.id("Password")).sendKeys(password);
        driver.findElement(By.id("sign-in")).click();

        waitForLoadingScreen();

        boolean init = true;
        // reporting the songs out of the list of CCLI songnumbers
        for (Song song : songList) {
            // search for the CCLI songnumber
            try {
                WebElement searchBar = driver.findElement(By.id("SearchIinput"));
                searchBar.clear();
                searchBar.sendKeys(song.getCcliNumber());
                if (init) {
                    driver.findElement(By.xpath("//*[@id=\"MainWrapper\"]/div/div[1]/div/main/div[1]/div[2]/div/div/div[2]/div/button[2]")).click();
                    init = false;
                } else {
                    driver.findElement(By.xpath("//*[@id=\"MainWrapper\"]/div/div[1]/div/main/div[1]/div[2]/div[1]/div/div[2]/div/button[2]")).click(); }
            } catch (NoSuchElementException e) {
                error(e, "Search Bar not found. The code of the website might have changed.\n" +
                        "Please report this to me so I can adapt my code to the changes.");
                break;
            }

            waitForLoadingScreen();

            try {
                // Opening the Report-From

                try {
                    driver.findElement(By.xpath("//*[@id=\"SearchResultsAlbums\"]/div[2]/div/div/div/div/table/tbody[1]/tr/td[7]/button")).click();
                } catch (NoSuchElementException e) {
//                    throw new NoSearchResultsException(song.getCcliNumber());
                    throw new SongNotReportableException(song);
                }

                waitForLoadingScreen();

                if (categories[0]) {
                    // increasing print count by 1
                    Select printCount = new Select(driver.findElement(By.id("cclPrint")));
                    printCount.selectByVisibleText("1");
                }

                if (categories[1]) {
                    // increasing digital count by 1
                    Select digitalCount = new Select(driver.findElement(By.id("cclDigital")));
                    digitalCount.selectByVisibleText("1");
                }

                if (categories[2]) {
                    // increasing stream count by 1
                    Select streamCount = new Select(driver.findElement(By.id("cclRecord")));
                    streamCount.selectByVisibleText("1");
                }

                if (categories[3]) {
                    // increasing translation count by 1
                    Select translationCount = new Select(driver.findElement(By.id("cclTranslate")));
                    translationCount.selectByVisibleText("1");
                }

                driver.findElement(By.xpath("//*[@id=\"ModalReportSongModal\"]/button/span")).click();

                // submitting the form
                //driver.findElement(By.xpath("//*[@id=\"ModalReportSongForm\"]/div[3]/button[2]")).click();
                song.markReported();
            } catch (SongNotReportableException e) {
                error(e, e.getMessage());
                if (e.getClass() == SongNotReportableException.class) {
                    song.markUnreported("Song not licensed or Website changed");
                }
//                else if (e.getClass() == NoSearchResultsException.class) {
//                    song.markUnreported("No search result for this CCLI number");
//                }
            }

        }
        errorWriter.close();
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/summaryGUI.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            error(e, e.getMessage());
        }

        if (root != null) {
            Stage summaryStage = Test.primaryStage;
            summaryStage.setTitle("Summary");
            summaryStage.setScene(new Scene(root, 600, 401));
            summaryStage.setResizable(false);

            SummaryGUIController summaryGUIController = fxmlLoader.getController();
            try {
                summaryGUIController.summarise(songList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            summaryStage.show();
        }
    }

    private void waitForLoadingScreen() {
        boolean isloaded = false;
        int tries = 0;

        while (!isloaded && tries < 10) {
            try {
                driver.findElement(By.xpath("//*[@id=\"page-loading-overlay\"]"));
                isloaded = true;
            } catch (NoSuchElementException e) {
                tries++;
                if (tries >= 10) {
                    error(e, "Timeout while waiting for loading screen. The code of the website might \n" +
                            "have changed. Please report this to me so I can adapt my code to the changes.");
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

        try {
            while (driver.findElement(By.xpath("//*[@id=\"page-loading-overlay\"]")).getAttribute("aria-busy").equals("true")) {
                System.out.println("waiting");
            }
            System.out.println("finished waiting");
        } catch (NoSuchElementException e) {
            System.out.println("finished waiting (element not found)");
        }
    }
}
