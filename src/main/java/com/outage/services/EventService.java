package com.outage.services;

import com.outage.entities.Event;
import com.outage.enums.EventSource;
import com.outage.repositories.EventRepository;
import com.outage.scrapers.ElectroHold;
import com.outage.scrapers.ElectroHoldEventDTO;
import com.outage.scrapers.SofiyskaVoda;
import com.outage.scrapers.SofiyskaVodaEventDTO;
import io.micronaut.context.annotation.Bean;
import io.micronaut.data.exceptions.EmptyResultException;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Bean
public class EventService {
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    @Inject
    EventRepository eventRepository;

    public Event getById(long id) {
        try {
            return eventRepository.findById(id);
        } catch (EmptyResultException e) {
            return null;
        }
    }

    public Iterable<Event> getAll() {
        return eventRepository.findAll();
    }

    public List<Event> getBySource(EventSource eventSource) {
        return eventRepository.findByEventSource(eventSource);
    }

    public List<Event> getByText(String text) {
        return eventRepository.findByText(text);
    }

    public List<Event> getByTextLike(String text) {
        return eventRepository.findByTextLike(text);
    }

    public List<Event> getByLocation(String location) {
        return eventRepository.findByLocation(location);
    }

    public List<Event> getByLocationLike(String location) {
        return eventRepository.findByLocationLike(location);
    }

    public List<Event> getByStartDate(LocalDateTime startDate) {
        return eventRepository.findByStartDate(startDate);
    }

    public List<Event> getByEndDate(LocalDateTime endDate) {
        return eventRepository.findByEndDate(endDate);
    }

    public List<Event> getByStartDateBefore(LocalDateTime localDateTime) {
        return eventRepository.findByStartDateBefore(localDateTime);
    }

    public List<Event> getByEndDateAfter(LocalDateTime localDateTime) {
        return eventRepository.findByEndDateAfter(localDateTime);
    }

    public List<Event> getByCreationDate(LocalDateTime localDateTime) {
        return eventRepository.findByCreationDate(localDateTime);
    }

    public List<Event> getByLastUpdateDate(LocalDateTime localDateTime) {
        return eventRepository.findByLastUpdateDate(localDateTime);
    }

    public Event saveEvent(Event event) {
        try {
            return eventRepository.save(event);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

    public void updateEvent(Event event) {
        try {
             eventRepository.updateEvent(event);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    @Scheduled(initialDelay = "10s", fixedRate = "120s")
    public void collectElectroHoldEvents() {
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

        int countAllEvents = dtos.size();
        int countSavedEvents = 0;
        int countUpdatedEvents = 0;

        for (ElectroHoldEventDTO dto : dtos) {
            Event event = Event.mapToEntity(dto);

            // Update any existing events
            List<Event> existingEvents = eventRepository.findByText(event.getText());

            for (Event eventFromDB : existingEvents) {
                if (!eventFromDB.getEventSource().equals(EventSource.ELECTRO_HOLD))
                    continue;

                eventFromDB.setLocation(dto.getLocation());
                eventFromDB.setAdditionalText(dto.getAdditionalText());
                eventFromDB.setStartDate(dto.getStartTime());
                eventFromDB.setEndDate(dto.getEndTime());

                eventFromDB.setLastUpdateDate(now);
                try {
                    updateEvent(eventFromDB);
                    countUpdatedEvents += 1;
                } catch (Exception ignored) {
                }
            }

            // Create new events
            if (existingEvents.isEmpty()) {
                if (saveEvent(event) != null) {
                    logger.info("Saved event " + event);
                    countSavedEvents += 1;
                }
            }
        }

        logger.info(String.format("Found %d events. Saved: %d events. Updated: %d events.", countAllEvents, countSavedEvents, countUpdatedEvents));
    }

    @Scheduled(initialDelay = "40s", fixedRate = "120s")
    public void collectSofiyskaVodaEvents() {
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

        int countAllEvents = dtos.size();
        int countSavedEvents = 0;
        int countUpdatedEvents = 0;

        for (SofiyskaVodaEventDTO dto : dtos) {
            Event event = Event.mapToEntity(dto);

            // Update any existing events
            List<Event> existingEvents = getByText(event.getText());

            for (Event eventFromDB : existingEvents) {
                if (!eventFromDB.getEventSource().equals(EventSource.SOFIYSKA_VODA))
                    continue;

                eventFromDB.setLocation(dto.getLocation());
                eventFromDB.setAdditionalText(dto.getAdditionalText());
                eventFromDB.setStartDate(dto.getStartTime());
                eventFromDB.setEndDate(dto.getEndTime());

                eventFromDB.setLastUpdateDate(now);
                try {
                    updateEvent(eventFromDB);
                    countUpdatedEvents += 1;
                } catch (Exception ignored) {
                }
            }

            // Create new events
            if (existingEvents.isEmpty()) {
                if (saveEvent(event) != null) {
                    logger.info("Saved event " + event);
                    countSavedEvents += 1;
                }
            }
        }

        logger.info(String.format("Found %d events. Saved: %d events. Updated: %d events.", countAllEvents, countSavedEvents, countUpdatedEvents));
    }

}
