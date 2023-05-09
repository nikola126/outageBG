package com.outage;

import com.outage.scrapers.ElectroHold;
import com.outage.scrapers.ElectroHoldEventDTO;
import com.outage.scrapers.SofiyskaVoda;
import com.outage.scrapers.SofiyskaVodaEventDTO;

import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        List<ElectroHoldEventDTO> electroHoldEventDTOList = new ArrayList<>();
        try {
            ElectroHold electroHold = new ElectroHold();
            electroHold.scrapePowerStops();
            electroHoldEventDTOList = new ArrayList<>(electroHold.getEventDTOS());
        } catch (Exception e) {
            System.out.println("EXCEPTION:" + e.getMessage());
        }

        for (ElectroHoldEventDTO dto : electroHoldEventDTOList)
            System.out.println(dto.toString());

        List<SofiyskaVodaEventDTO> sofiyskaVodaEventDTOList = new ArrayList<>();
        try {
            SofiyskaVoda sofiyskaVoda = new SofiyskaVoda();
            sofiyskaVoda.scrapeWaterStops();
            sofiyskaVodaEventDTOList = new ArrayList<>(sofiyskaVoda.getEventDTOS());
        } catch (Exception e) {
            System.out.println("EXCEPTION:" + e.getMessage());
        }

        for (SofiyskaVodaEventDTO dto : sofiyskaVodaEventDTOList)
            System.out.println(dto.toString());
    }
}