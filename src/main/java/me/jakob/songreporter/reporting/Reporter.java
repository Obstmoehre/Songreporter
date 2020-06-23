package me.jakob.songreporter.reporting;

import io.github.bonigarcia.wdm.WebDriverManager;
import me.jakob.songreporter.GUI.ErrorGUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Reporter {
    private File script;
    private WebDriver driver;
    private final File errorLog = new File(System.getProperty("user.home") + "/Songreporter/error.log");

    public void report(String eMail, String password, String browser, File script, ArrayList<String> ccliList) throws IOException {
        FileWriter errorWriter = new FileWriter(errorLog, true);
        this.script = script;

        // starting driver and going to main page
        switch(browser) {
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

        // reporting the songs out of the list of CCLI songnumbers
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
            } catch(org.openqa.selenium.NoSuchElementException | InterruptedException e) {
                if (!errorLog.exists()) {
                    if (!(errorLog.createNewFile())) {
                        new ErrorGUI().showNewErrorMessage("Failed to create new error log.");
                    }
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
        FileWriter fileWriter = new FileWriter(this.script, true);
        fileWriter.append("#reported");
        fileWriter.flush();
        fileWriter.close();
    }
}
