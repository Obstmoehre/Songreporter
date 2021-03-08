package me.jakob.songreporter.reporting.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import me.jakob.songreporter.reporting.enums.WebsiteElement;
import me.jakob.songreporter.reporting.exceptions.CCLILoginException;
import me.jakob.songreporter.reporting.exceptions.ServiceNotInitializedException;
import me.jakob.songreporter.reporting.exceptions.WebsiteChangedException;
import me.jakob.songreporter.reporting.exceptions.WrongCredentialsException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;

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

    public HashMap<String, String> getCookies() {
        return cookies;
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

        initialWaitForLoadingScreen();
        this.cookies = new HashMap<>();
        for (Cookie cookie : this.driver.manage().getCookies()) {
            this.cookies.put(cookie.getName(), cookie.getValue());
        }
        return true;
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
                }
            }
        }

        try {
            while (driver.findElement(By.xpath("//*[@id=\"page-loading-overlay\"]"))
                    .getAttribute("aria-busy").equals("true")) {
                //noinspection BusyWait
                Thread.sleep(50);
            }
        } catch (NoSuchElementException | InterruptedException e) {
        }
    }
}
