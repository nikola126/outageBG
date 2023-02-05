package com.outage;

import com.outage.scrapers.ElectroHold;
import com.outage.scrapers.SofiyskaVoda;

public class Application {
    public static void main(String[] args) {
//        Micronaut.run(Application.class, args);

        System.out.println("Hello world");
        SofiyskaVoda sofiyskaVoda = new SofiyskaVoda();
        ElectroHold electroHold = new ElectroHold();
        try {
            electroHold.scrapePowerStops();
        } catch (Exception e) {
            System.out.println("EXCEPTION:" + e.getMessage());
        }

        try {
            sofiyskaVoda.scrapeWaterStops();
        } catch (Exception e) {
            System.out.println("EXCEPTION:" + e.getMessage());
        }

    }
}