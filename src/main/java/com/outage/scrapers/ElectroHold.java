package com.outage.scrapers;

import com.outage.WebDrivers;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
public class ElectroHold {
    private Set<ElectroHoldEventDTO> eventDTOS = new LinkedHashSet<>();
    public void scrapePowerStops() throws Exception {
        String powerStopsURL = "https://info.electrohold.bg/webint/vok/avplan.php";
        WebDriver webDriver = WebDrivers.getFirefoxWebDriver();
        Actions actions = new Actions(webDriver);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
        webDriver.get(powerStopsURL);
        Thread.sleep(2000);

        try {
            WebElement rowElement = webDriver.findElement(By.id("row"));
            List<WebElement> cardElements = rowElement.findElements(By.className("card-body"));

            int lastCardElementIndex = 2;


            for (int i = 2; i < cardElements.size(); i++) {
                WebElement card = cardElements.get(lastCardElementIndex);
                lastCardElementIndex += 1;
                javascriptExecutor.executeScript("arguments[0].scrollIntoView(false);", card);
                actions.moveToElement(card).click().build().perform();
                Thread.sleep(200);

                WebElement list = null;
                try {
                    list = card.findElement(By.tagName("ul"));
                } catch (NoSuchElementException e) {
                    continue;
                }

                List<WebElement> listItems = list.findElements(By.tagName("li"));

                int lastListItemIndex = 0;
                for (int j = 0; j < listItems.size(); j++) {
                    WebElement li = listItems.get(lastListItemIndex);
                    lastListItemIndex += 1;
                    javascriptExecutor.executeScript("arguments[0].scrollIntoView(false);",li);
                    actions.moveToElement(li).click().build().perform();
                    Thread.sleep(1000);

                    String mapId = li.getAttribute("id");
                    mapId = mapId.substring(0, mapId.indexOf("_"));
                    WebElement mapComponent = webDriver.findElement(By.id(mapId));

                    // Enter fullscreen
                    List<WebElement> buttons = mapComponent.findElements(By.tagName("button"));
                    for (WebElement button : buttons) {
                        boolean success = false;
                        for (int attempt = 0; attempt < 10; attempt++) {
                            try {
                                if (button.getAttribute("class") != null &&
                                        button.getAttribute("class").equalsIgnoreCase("gm-control-active gm-fullscreen-control")) {
                                    button.click();
                                    Thread.sleep(1200);
                                    success = true;
                                    break;
                                }
                            } catch (ElementClickInterceptedException e) {
                                Thread.sleep(500);
                            }
                        }
                        if (success)
                            break;
                    }

                    mapComponent = webDriver.findElement(By.id(mapId));
                    List<WebElement> tags = mapComponent.findElements(By.tagName("img"));

                    for (WebElement tag : tags) {
                        if (!tag.getAttribute("src").contains("transparent"))
                            continue;

                        javascriptExecutor.executeScript("arguments[0].scrollIntoView(false);",tag);
                        actions.moveToElement(tag).click().build().perform();
                        Thread.sleep(50);

                        parseEvents(webDriver, mapId);
                    }

                    // Exit fullscreen
                    buttons = mapComponent.findElements(By.tagName("button"));
                    for (WebElement button : buttons) {
                        if (button.getAttribute("class") != null &&
                                button.getAttribute("class").equalsIgnoreCase("gm-control-active gm-fullscreen-control")) {
                            button.click();
                            Thread.sleep(1200);
                            break;
                        }
                    }

                    // Update all components before continuing
                    rowElement = webDriver.findElement(By.id("row"));
                    cardElements = rowElement.findElements(By.className("card-body"));
                }

            }
        } catch (Exception e) {
            System.out.println("Error scraping power stops!");
            e.printStackTrace();
            webDriver.quit();
        } finally {
            webDriver.quit();
        }

        try {
            webDriver.close();
        } catch (Exception ignored) {}
    }

    private void parseEvents(WebDriver webDriver, String mapId) {
        Document test = Jsoup.parse(webDriver.getPageSource());

        String eventText = "";
        Element ul = test.getElementById(mapId)
                .getElementsByTag("ul")
                .stream()
                .filter(el -> el.text().toLowerCase().contains("тип прекъсване"))
                .findFirst().orElse(null);

        if (ul != null) {
            eventText = ul.text();
        }

        if (!eventText.isEmpty()) {
            try {
                eventDTOS.add(convertTextToDTO(eventText));
            } catch (Exception e) {
                System.out.println("Error extracting ElectroHold event!");
            }
        }
    }

    public ElectroHoldEventDTO convertTextToDTO(String text) throws Exception {
        ElectroHoldEventDTO dto = new ElectroHoldEventDTO();

        // Населено място: ТОМПСЪН Тип прекъсване: непланирано Начало на прекъсването: 09.05.2023 18:13 Очаквано време за възстановяване на захранването: 09.05.2023 19:30
        dto.setText(text);

        int indexOfLocation = text.indexOf("Населено място:");
        int indexOfAdditionalText = text.indexOf("Тип прекъсване:");
        int indexOfStartTime = text.indexOf("Начало на прекъсването:");
        int indexOfEndTime = text.indexOf("Очаквано време за възстановяване на захранването:");

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
        dto.setLocation(sb.toString().trim());

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
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime startTime = LocalDateTime.parse(sb.toString().trim(), dtf);
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
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime endTime = LocalDateTime.parse(sb.toString().trim(), dtf);
            dto.setEndTime(endTime);
        } catch (Exception e) {
            LocalDateTime now = LocalDateTime.now();
            now = now.plusHours(24);
            dto.setEndTime(now);
        }

        return dto;
    }

}
