package com.outage.repositories;

import com.outage.entities.Event;
import com.outage.enums.EventSource;
import io.micronaut.context.annotation.Executable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    @Executable
    Event findById(long id);

    @Executable
    List<Event> findByLastUpdateDate(LocalDateTime lastUpdateDate);

    @Executable
    List<Event> findByCreationDate(LocalDateTime creationDate);

    @Executable
    List<Event> findByEventSource(EventSource eventSource);

    @Executable
    List<Event> findByTextLike(String text);

    @Executable
    List<Event> findByText(String text);

    @Executable
    List<Event> findByLocationLike(String location);

    @Executable
    List<Event> findByLocation(String location);

    @Executable
    List<Event> findByStartDate(LocalDateTime startDate);

    @Executable
    List<Event> findByEndDate(LocalDateTime endDate);

    @Executable
    List<Event> findByStartDateBefore(LocalDateTime startDate);

    @Executable
    List<Event> findByEndDateAfter(LocalDateTime endDate);

    @Executable
    default void updateEvent(Event event) {
        update(event.getId(),
                event.getEventSource(),
                event.getLastUpdateDate(),
                event.getRemovalDate(),
                event.getText(),
                event.getLocation(),
                event.getAdditionalText(),
                event.getStartDate(),
                event.getEndDate());
    }

    @Executable
    void update(@Id long id,
                EventSource eventSource,
                LocalDateTime lastUpdateDate,
                LocalDateTime removalDate,
                String text,
                String location,
                String additionalText,
                LocalDateTime startDate,
                LocalDateTime endDate);

}
