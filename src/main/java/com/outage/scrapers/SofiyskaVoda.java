package com.outage.scrapers;

import com.outage.WebDrivers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.List;

public class SofiyskaVoda {

    public void scrapeWaterStops() throws Exception {
        String waterStopsURL = "https://www.sofiyskavoda.bg/gis/?a=0";
        WebDriver webDriver = WebDrivers.getWebDriver();
        Actions actions = new Actions(webDriver);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
        webDriver.get(waterStopsURL);
        Thread.sleep(2000);

        try {
            WebElement accordionElement = webDriver.findElement(By.id("divAccordianContainer"));

            List<WebElement> listAccordionElements = accordionElement.findElements(By.xpath("*"));

            // click through all children to fetch all data
            for (int i = 0; i < listAccordionElements.size(); i++) {
                try {
                    if ((i + 1) % 2 != 0) {
                        WebElement toClick = listAccordionElements.get(i);
                        javascriptExecutor.executeScript("arguments[0].scrollIntoView(false);", toClick);
                        actions.moveToElement(toClick).click().build().perform();
                        Thread.sleep(250);

                        actions.moveToElement(toClick).click().build().perform();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error clicking");
                }
            }

            parseEvents(webDriver);
        } catch (Exception e) {
            System.out.println("Error parsing!");
            e.printStackTrace();
            webDriver.quit();
        } finally {
            webDriver.quit();
        }
    }

    private void parseEvents(WebDriver webDriver) {
        List<String> expectedParentIds = Arrays.asList("sanitaryBackupContent", "infrastructureAlertsContent");
        Document document = Jsoup.parse(webDriver.getPageSource());

        List<Element> contents = document.getElementsByClass("scrollbar_content").stream().filter(el -> el.childrenSize() > 0).toList();

        for (Element content : contents) {
            Element table = content.getElementsByClass("tableWaterStopInfo").first();
            if (table == null)
                continue;

            Element parent = table.parent();

            if (parent == null || !expectedParentIds.contains(parent.attr("id")))
                continue;

            Elements tds = table.getElementsByTag("td");

            for (Element td : tds) {
                System.out.println("EVENT: " + td.text());
            }
        }
    }
}
