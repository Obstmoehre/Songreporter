package me.jakob.songreporter.reporting.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import me.jakob.songreporter.GUI.elements.ErrorGUI;
import me.jakob.songreporter.reporting.enums.WebsiteElement;
import me.jakob.songreporter.reporting.exceptions.CCLILoginException;
import me.jakob.songreporter.reporting.exceptions.TimeoutException;
import me.jakob.songreporter.reporting.exceptions.WebsiteChangedException;
import me.jakob.songreporter.reporting.exceptions.WrongCredentialsException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.function.Function;

public class SeleniumService {
    private WebDriver driver;
    private final String browser;
    private final File errorLog = new File(System.getProperty("user.home") + "/Songreporter/error.log");
    private FileWriter errorWriter;

    public SeleniumService(String browser) {
        this.browser = browser;
    }

    public boolean login(String eMail, String password) throws CCLILoginException, InterruptedException {
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
            waitForPageLoad();

            // waiting for login page
            boolean loaded = false;
            int triesBeforeTimeout = 0;
            while (!loaded) {
                try {
                    driver.findElement(By.id("EmailAddress"));
                    loaded = true;
                } catch (NoSuchElementException e) {
                    triesBeforeTimeout++;
                    if (triesBeforeTimeout >= 30) {
                        throw new TimeoutException();
                    }
                    Thread.sleep(300);
                }
            }

            // login to online reporting
            try {
                try {
                    driver.findElement(By.id("EmailAddress")).sendKeys(eMail);
                } catch (NoSuchElementException e) {
                    throw new WebsiteChangedException(WebsiteElement.E_MAIL_FIELD);
                }
                Thread.sleep(50);

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
                    throw e;
                }
            }
        }

        initialWaitForLoadingScreen();
        return true;
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

    private void initialWaitForLoadingScreen() throws CCLILoginException, InterruptedException {
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
                        throw new WebsiteChangedException(WebsiteElement.LOADING_SCREEN);
                    } else {
                        throw new WrongCredentialsException();
                    }
                } else if (!checkLogin()) {
                    throw new WrongCredentialsException();
                }
                Thread.sleep(10);
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
        }
    }

    private boolean checkLogin() {
        try {
            return !driver.findElement(By.xpath("//*[@id=\"sign-into-account\"]/div[1]/div/div/p[2]"))
                    .getText().contains("Die E-Mailadresse oder das Passwort wurden nicht gefunden.");
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return true;
        }
    }

    private void waitForPageLoad() {
        Wait<WebDriver> wait = new WebDriverWait(driver, 30);
        wait.until(driver -> {
            System.out.println("Current Window State: "
                    + ((JavascriptExecutor) driver).executeScript("return document.readyState"));
            return String
                    .valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"))
                    .equals("complete");
        });
    }
}
