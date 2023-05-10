package com.outage.services;

import com.outage.entities.ElectroHoldEvent;
import com.outage.repositories.ElectroHoldEventRepository;
import com.outage.scrapers.ElectroHold;
import com.outage.scrapers.ElectroHoldEventDTO;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Singleton
public class ElectroHoldEventService {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    @Inject
    ElectroHoldEventRepository electroHoldEventRepository;

    public Iterable<ElectroHoldEvent> getAllEvents() {
        return electroHoldEventRepository.findAll();
    }

    public ElectroHoldEvent getById(long id) {
        return electroHoldEventRepository.findById(id);
    }

    public List<ElectroHoldEvent> getByText(String text) {
        return electroHoldEventRepository.findByText(text);
    }

    @Scheduled(initialDelay = "10s", fixedRate = "10m")
    public void scrapeEvents() {
        logger.info("Started scheduled event scrape.");
        LocalDateTime now = LocalDateTime.now();

        Set<ElectroHoldEventDTO> dtos = new LinkedHashSet<>();
        try {
            ElectroHold electroHold = new ElectroHold();
            electroHold.scrapePowerStops();
            dtos = electroHold.getEventDTOS();
        } catch (Exception e) {
            logger.warning("Error scraping events!");
        }

        for (ElectroHoldEventDTO dto : dtos) {
            ElectroHoldEvent event = ElectroHoldEvent.mapToEntity(dto);

            // Update any existing events
            List<ElectroHoldEvent> existingEvents = electroHoldEventRepository.findByText(event.getText());

            for (ElectroHoldEvent eventFromDB : existingEvents) {
                eventFromDB.setLastUpdateDate(now);
                electroHoldEventRepository.update(eventFromDB);
            }

            // Create new events
            if (existingEvents.isEmpty()) {
                electroHoldEventRepository.save(event);
                logger.info("Added " + event);
            }
        }

    }
}
