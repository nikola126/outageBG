package com.outage.scrapers;

import com.outage.WebDrivers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.List;

public class ElectroHold {
    public void scrapePowerStops() throws Exception {
        String powerStopsURL = "https://info.electrohold.bg/webint/vok/avplan.php";
        WebDriver webDriver = WebDrivers.getWebDriver();
        Actions actions = new Actions(webDriver);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
        webDriver.get(powerStopsURL);
        Thread.sleep(2000);

        try {
            WebElement rowElement = webDriver.findElement(By.id("row"));

            List<WebElement> cardElements = rowElement.findElements(By.className("card-body"));

            for (int i = 2; i < cardElements.size(); i++) {
                WebElement card = cardElements.get(i);
                // javascriptExecutor.executeScript("arguments[0].scrollIntoView(false);", card);
                actions.moveToElement(card).click().build().perform();
                Thread.sleep(200);

                WebElement list = null;
                try {
                    list = card.findElement(By.tagName("ul"));
                } catch (NoSuchElementException e) {
                    continue;
                }

                List<WebElement> listItems = list.findElements(By.tagName("li"));
                System.out.println("List items: " + listItems.size());

                for (WebElement li : listItems) {
                    System.out.println("Li: " + li.getText());
                    javascriptExecutor.executeScript("arguments[0].scrollIntoView(false);",li);
                    actions.moveToElement(li).click().build().perform();
                    Thread.sleep(1000);

                    String mapId = li.getAttribute("id");
                    mapId = mapId.substring(0, mapId.indexOf("_"));
                    System.out.println("Map id: " + mapId);
                    WebElement mapComponent = webDriver.findElement(By.id(mapId));

                    List<WebElement> tags = mapComponent.findElements(By.tagName("img"));
                    System.out.println("Tags: " + tags.size());

                    for (WebElement tag : tags) {
                        if (!tag.getAttribute("src").contains("transparent"))
                            continue;

                        javascriptExecutor.executeScript("arguments[0].scrollIntoView(false);",tag);
                        actions.moveToElement(tag).click().build().perform();
                        Thread.sleep(50);

                        Document test = Jsoup.parse(webDriver.getPageSource());
                        System.out.println("Clicked tag!");

                        Element ul = test.getElementById(mapId).getElementsByTag("ul").stream().filter(el -> el.text().toLowerCase().contains("тип прекъсване")).findFirst().orElse(null);
                        if (ul != null)
                            System.out.println("Ul: " + ul.text());

//                        WebElement toastContainer = webDriver.findElement(By.id("toast-container"));
//                        System.out.println("Toast: " +toastContainer.getText());

                    }

                    // actions.moveToElement(li).click().build().perform();
                }

            }
        } catch (Exception e) {
            System.out.println("Error parsing!");
            e.printStackTrace();
            webDriver.quit();
        } finally {
            webDriver.quit();
        }
    }

    private void parseEvents(WebDriver webDriver) {
        Document document = Jsoup.parse(webDriver.getPageSource());

        Elements imgElements = document.getElementsByTag("img");

        System.out.println("Images: " + imgElements.size());
    }
}
