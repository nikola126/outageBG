package com.outage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class WebDrivers {
    public static WebDriver getWebDriver() throws MalformedURLException {
        System.out.println("Web Driver Setup");
        FirefoxOptions options = new FirefoxOptions();

        options.addArguments(List.of("--window-position=0,0"));
        options.addArguments(List.of("--window-size=1440,900"));
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-application-cache");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments(List.of("-headless"));

        return new RemoteWebDriver(new URL("http://localhost:4444"), options);
    }
}
