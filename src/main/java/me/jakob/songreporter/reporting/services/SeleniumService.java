package me.jakob.songreporter.reporting.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import me.jakob.songreporter.GUI.elements.ErrorGUI;
import me.jakob.songreporter.reporting.enums.WebsiteElement;
import me.jakob.songreporter.reporting.exceptions.*;
import me.jakob.songreporter.reporting.exceptions.TimeoutException;
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
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

public class SeleniumService {
    private WebDriver driver;
    private String browser;
    private static SeleniumService instance;
    private boolean initiated;
    private HashMap<String, String> cookies;

    public static SeleniumService getInstance() {
        if (SeleniumService.instance == null) {
            SeleniumService.instance = new SeleniumService();
        }
        return SeleniumService.instance;
    }

    private SeleniumService() {
        this.initiated = false;
    }

    public void init(String browser) {
        this.browser = browser;
        this.initiated = true;
    }

    public boolean login(String eMail, String password) throws CCLILoginException, InterruptedException, ServiceNotInitializedException {
        if (!this.initiated) {
            throw new ServiceNotInitializedException();
        }

        switch (browser) {
            case "Chrome":
                WebDriverManager.chromedriver().setup();
                this.driver = new ChromeDriver();
                break;
            case "Firefox":
                WebDriverManager.firefoxdriver().setup();
                this.driver = new FirefoxDriver();
                break;
            case "Opera":
                WebDriverManager.operadriver().setup();
                this.driver = new OperaDriver();
                break;
        }

        int retries = 0;
        int maxRetries = 3;

        while (retries < maxRetries) {
            this.driver.get("https://olr.ccli.com");
            waitForPageLoad();

            // waiting for login page
            //boolean loaded = false;
            //int triesBeforeTimeout = 0;
            //while (!loaded) {
            //    try {
            //        driver.findElement(By.id("EmailAddress"));
            //        loaded = true;
            //    } catch (NoSuchElementException e) {
            //        triesBeforeTimeout++;
            //        if (triesBeforeTimeout >= 30) {
            //            throw new TimeoutException();
            //        }
            //        Thread.sleep(300);
            //    }
            //}

            // login to online reporting
            try {
                try {
                    this.driver.findElement(By.id("EmailAddress")).sendKeys(eMail);
                } catch (NoSuchElementException e) {
                    throw new WebsiteChangedException(WebsiteElement.E_MAIL_FIELD);
                }
                Thread.sleep(50);

                try {
                    this.driver.findElement(By.id("Password")).sendKeys(password);
                } catch (NoSuchElementException e) {
                    throw new WebsiteChangedException(WebsiteElement.PASSWORD_FIELD);
                }

                try {
                    this.driver.findElement(By.id("sign-in")).click();
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

        waitForPageLoad();
        if (checkLogin()) {
            this.cookies = new HashMap<>();
            for (Cookie cookie : this.driver.manage().getCookies()) {
                this.cookies.put(cookie.getName(), cookie.getValue());
            }
            return true;
        } else {
            throw new WrongCredentialsException();
        }
    }

    private boolean checkLogin() {
        try {
            return !this.driver.findElement(By.xpath("//*[@id=\"sign-into-account\"]/div[1]/div/div/p[2]"))
                    .getText().contains("Die E-Mailadresse oder das Passwort wurden nicht gefunden.");
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return true;
        }
    }

    private void waitForPageLoad() {
        Wait<WebDriver> wait = new WebDriverWait(this.driver, 30);
        wait.until(driver -> {
            System.out.println("Current Window State: "
                    + ((JavascriptExecutor) driver).executeScript("return document.readyState"));
            return String
                    .valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"))
                    .equals("complete");
        });
    }
}
