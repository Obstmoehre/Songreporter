package me.jakob.songreporter.reporting;

import io.github.bonigarcia.wdm.WebDriverManager;
import me.jakob.songreporter.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Reporter {
    private static ChromeDriverService service;
    private WebDriver driver;
    private ConfigManager configManager;
    private final File errorLog = new File(System.getProperty("user.home") + "/Songreporter/error.log");

    public void report(ConfigManager configManager, ArrayList<String> ccliList) throws IOException {
        FileWriter errorWriter = new FileWriter(errorLog, true);
        this.configManager = configManager;

        // starting driver and going to main page
        switch(configManager.getBrowser()) {
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

        driver.findElement(By.id("EmailAddress")).sendKeys(configManager.getTempEMail());
        driver.findElement(By.id("Password")).sendKeys(configManager.getTempPassword());
        driver.findElement(By.id("sign-in")).click();

        // reporting the songs out of the list of CCLI songnumbers
        byte count = 0;
        for (String ccli : ccliList){
            try {
                // search for the CCLI songnumber
                WebElement searchBar = driver.findElement(By.id("SearchTerm"));
                searchBar.clear();
                searchBar.sendKeys(ccli);
                driver.findElement(By.xpath("//*[@id=\"searchBar\"]/div/div/button")).click();

                // Extracting the internal songnumber and expanding the reporting field
                String songNumber = driver.findElement(By.className("searchResultsSongSummary")).getAttribute("id");
                driver.findElement(By.xpath("//*[@id=\"" + songNumber + "\"]/div/div[1]/span[1]")).click();
                songNumber = songNumber.substring(5);

                // setting digital count to 1
                WebElement digitalCount = driver.findElement(By.id("DigitalCount-" + songNumber));
                Thread.sleep(750);
                digitalCount.click();
                digitalCount.clear();
                digitalCount.sendKeys("1");

                // submitting the form and removing the CCLI songnumber
                driver.findElement(By.xpath("//*[@id=\"AddCCL-" + songNumber + "\"]/div[1]/div[4]/button")).click();
                count++;
            } catch(org.openqa.selenium.NoSuchElementException | InterruptedException e) {
                if (!errorLog.exists()) {
                    errorLog.createNewFile();
                }
                errorWriter.write("Error while reporting:\n" + e.getMessage() + "\n\n");
                errorWriter.flush();
                e.printStackTrace();
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
    }

    private void markAsReported() throws IOException {
        FileWriter fileWriter = new FileWriter(configManager.getScript(), true);
        fileWriter.append("#reported");
        fileWriter.flush();
        fileWriter.close();
    }
}
