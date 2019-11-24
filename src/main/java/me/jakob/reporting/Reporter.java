package me.jakob.reporting;

import me.jakob.GUI.ErrorGUI;
import me.jakob.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Reporter {
    private static transient ChromeDriverService service;
    private transient WebDriver driver;
    private ConfigManager configManager;

    public void report(ConfigManager configManager, ArrayList<String> ccliList) {
        this.configManager = configManager;

        // starting driver and going to main page
        try {
            if (this.configManager.getDriverPath().contains("chrome")) {
                createAndStartChromeService();
                createChromeDriver();
            } else if (this.configManager.getDriverPath().contains("gecko")) {
                createAndStartFirefoxService();
                createFirefoxDriver();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver.get("https:/olr.ccli.com");

        // login to online me.jakob.reporting
        driver.findElement(By.id("EmailAddress")).sendKeys(configManager.getTempEMail());
        driver.findElement(By.id("Password")).sendKeys(configManager.getTempPassword());
        driver.findElement(By.id("Sign-In")).click();

        // me.jakob.reporting the songs out of the list of CCLI songnumbers
        byte count = 0;
        for (String ccli : ccliList){
            try {
                // search for the CCLI songnumber
                WebElement searchBar = driver.findElement(By.id("SearchTerm"));
                searchBar.clear();
                searchBar.sendKeys(ccli);
                driver.findElement(By.xpath("//*[@id=\"searchBar\"]/div/div/button")).click();

                // Extracting the internal songnumber and expanding the me.jakob.reporting field
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
                // me.jakob.GUI to show error messages that show up while me.jakob.reporting
                ErrorGUI errorGUI = new ErrorGUI();
                errorGUI.showNewErrorMessage("Ein Fehler ist aufgetreten:\n\n" + e.getMessage() + "\n\nDas" +
                        " " + (count+1) + ". Lied (CCLI: " + ccli + ") muss vermutlich nicht gemeldet werden.");
            }

            // a little delay for an animation on the website
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        driver.quit();
        service.stop();

        // a little delay so you have a chance to make a screenshot of error windows or read them
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private void markAsReported() throws IOException {
        FileWriter fileWriter = new FileWriter(configManager.getScript());
        fileWriter.append("#reported");
    }

    // functions to create browserdrivers and start a browser window

    private void createAndStartChromeService() throws IOException {
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(this.configManager.getDriverPath()))
                .usingAnyFreePort()
                .build();
        service.start();
    }

    private void createAndStartFirefoxService() throws IOException {
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(this.configManager.getDriverPath()))
                .usingAnyFreePort()
                .build();
        service.start();
    }

    private void createChromeDriver() {
        driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.chrome());
    }

    private void createFirefoxDriver() {
        driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.firefox());
    }
}
