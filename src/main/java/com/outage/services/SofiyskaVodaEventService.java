package com.outage.services;

import com.outage.entities.SofiyskaVodaEvent;
import com.outage.repositories.SofiyskaVodaEventRepository;
import com.outage.scrapers.SofiyskaVoda;
import com.outage.scrapers.SofiyskaVodaEventDTO;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Singleton
public class SofiyskaVodaEventService {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    @Inject
    SofiyskaVodaEventRepository sofiyskaVodaEventRepository;

    public Iterable<SofiyskaVodaEvent> getAllEvents() {
        return sofiyskaVodaEventRepository.findAll();
    }

    public SofiyskaVodaEvent getById(long id) {
        return sofiyskaVodaEventRepository.findById(id);
    }

    public List<SofiyskaVodaEvent> getByText(String text) {
        return sofiyskaVodaEventRepository.findByText(text);
    }

    @Scheduled(initialDelay = "10s", fixedRate = "10m")
    public void scrapeEvents() {
        logger.info("Started scheduled event scrape.");
        LocalDateTime now = LocalDateTime.now();

        Set<SofiyskaVodaEventDTO> dtos = new LinkedHashSet<>();
        try {
            SofiyskaVoda sofiyskaVoda = new SofiyskaVoda();
            sofiyskaVoda.scrapeWaterStops();
            dtos = sofiyskaVoda.getEventDTOS();
        } catch (Exception e) {
            logger.warning("Error scraping events!");
        }

        for (SofiyskaVodaEventDTO dto : dtos) {
            SofiyskaVodaEvent event = SofiyskaVodaEvent.mapToEntity(dto);

            // Update any existing events
            List<SofiyskaVodaEvent> existingEvents = sofiyskaVodaEventRepository.findByText(event.getText());

            for (SofiyskaVodaEvent eventFromDB : existingEvents) {
                eventFromDB.setLastUpdateDate(now);
                sofiyskaVodaEventRepository.update(eventFromDB);
            }

            // Create new events
            if (existingEvents.isEmpty()) {
                sofiyskaVodaEventRepository.save(event);
                logger.info("Added " + event);
            }
        }

    }
}
