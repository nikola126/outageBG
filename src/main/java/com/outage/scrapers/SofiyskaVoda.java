package com.outage.scrapers;

import com.outage.WebDrivers;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.*;

@Getter
@Setter
public class SofiyskaVoda {
    private Set<SofiyskaVodaEventDTO> eventDTOS = new LinkedHashSet<>();

    public void scrapeWaterStops() throws Exception {
        String waterStopsURL = "https://www.sofiyskavoda.bg/gis/?a=0";
        WebDriver webDriver = WebDrivers.getFirefoxWebDriver();
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

        try {
            webDriver.close();
        } catch (Exception ignored) {
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
                try {
                    eventDTOS.add(convertTextToDTO(td.text()));
                } catch (Exception e) {
                    System.out.println("Error extracting Sofiyska Voda event!");
                }
            }
        }
    }

    public SofiyskaVodaEventDTO convertTextToDTO(String text) throws Exception {
        SofiyskaVodaEventDTO dto = new SofiyskaVodaEventDTO();

        //
        dto.setText(text);

        int indexOfLocation = text.indexOf("Местоположение:");
        int indexOfAdditionalText = text.indexOf("Описание:");
        int indexOfStartTime = text.indexOf("Начало:");
        int indexOfEndTime = text.indexOf("Край:");

        if (indexOfLocation == -1 || indexOfAdditionalText == -1)
            throw new Exception();
        if (indexOfStartTime == -1 || indexOfEndTime == -1)
            throw new Exception();

        StringBuilder sb = new StringBuilder();

        boolean colonReached = false;
        for (int i = 0; i < text.length(); i++) {
            if (i < indexOfAdditionalText) {
                if (colonReached)
                    sb.append(text.charAt(i));
                if (text.charAt(i) == ':')
                    colonReached = true;
            }
        }
        String locationText = sb.toString().trim();
        if (locationText.contains("Зона на спиране"))
            locationText = locationText.split("Зона на спиране")[1];
        if (!Character.isAlphabetic(locationText.charAt(0)))
            locationText = locationText.substring(1);

        dto.setLocation(locationText);

        sb.setLength(0);

        colonReached = false;
        for (int i = 0; i < text.length(); i++) {
            if (i < indexOfAdditionalText)
                continue;
            if (i < indexOfStartTime) {
                if (colonReached)
                    sb.append(text.charAt(i));
                if (text.charAt(i) == ':')
                    colonReached = true;
            }
        }
        dto.setAdditionalText(sb.toString().trim());

        sb.setLength(0);

        colonReached = false;
        for (int i = 0; i < text.length(); i++) {
            if (i < indexOfStartTime)
                continue;
            if (i < indexOfEndTime) {
                if (colonReached)
                    sb.append(text.charAt(i));
                if (text.charAt(i) == ':')
                    colonReached = true;
            }
        }

        // If parsing fails, set it to beginning of the day
        try {
            String sbText = sb.toString().replaceAll(",", "");
            String[] tokens = sbText.trim().split(" ");
            int year = Integer.parseInt(tokens[2]);
            int month = 1;
            switch (tokens[1].trim().toLowerCase()) {
                case "януари" -> month = 1;
                case "февруари" -> month = 2;
                case "март" -> month = 3;
                case "април" -> month = 4;
                case "май" -> month = 5;
                case "юни" -> month = 6;
                case "юли" -> month = 7;
                case "август" -> month = 8;
                case "септември" -> month = 9;
                case "октомври" -> month = 10;
                case "ноември" -> month = 11;
                case "декември" -> month = 12;
            }
            int day = Integer.parseInt(tokens[0]);
            int hour = Integer.parseInt(tokens[3].split(":")[0]);
            int minutes = Integer.parseInt(tokens[3].split(":")[1]);
            int seconds = 0;

            LocalDateTime startTime = LocalDateTime.of(year, month, day, hour, minutes, seconds);
            dto.setStartTime(startTime);
        } catch (Exception e) {
            LocalDateTime now = LocalDateTime.now();
            now = now.minusSeconds(now.get(ChronoField.SECOND_OF_DAY));
            dto.setStartTime(now);
        }

        sb.setLength(0);

        colonReached = false;
        for (int i = 0; i < text.length(); i++) {
            if (i < indexOfEndTime)
                continue;
            if (colonReached)
                sb.append(text.charAt(i));
            if (text.charAt(i) == ':')
                colonReached = true;
        }

        // If parsing fails, set it to now + 24 hours
        try {
            String sbText = sb.toString().replaceAll(",", "");
            String[] tokens = sbText.trim().split(" ");
            int year = Integer.parseInt(tokens[2]);
            int month = 1;
            switch (tokens[1].trim().toLowerCase()) {
                case "януари" -> month = 1;
                case "февруари" -> month = 2;
                case "март" -> month = 3;
                case "април" -> month = 4;
                case "май" -> month = 5;
                case "юни" -> month = 6;
                case "юли" -> month = 7;
                case "август" -> month = 8;
                case "септември" -> month = 9;
                case "октомври" -> month = 10;
                case "ноември" -> month = 11;
                case "декември" -> month = 12;
            }
            int day = Integer.parseInt(tokens[0]);
            int hour = Integer.parseInt(tokens[3].split(":")[0]);
            int minutes = Integer.parseInt(tokens[3].split(":")[1]);
            int seconds = 0;

            LocalDateTime endTime = LocalDateTime.of(year, month, day, hour, minutes, seconds);
            dto.setEndTime(endTime);
        } catch (Exception e) {
            LocalDateTime now = LocalDateTime.now();
            now = now.plusHours(24);
            dto.setEndTime(now);
        }

        return dto;
    }
}
