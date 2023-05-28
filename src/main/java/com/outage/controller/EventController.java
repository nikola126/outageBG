package com.outage.controller;

import com.outage.entities.Event;
import com.outage.enums.EventSource;
import com.outage.services.EventService;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;

import java.util.List;

@Controller("/events")
public class EventController {
    @Inject
    EventService eventService;

    @Get("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Iterable<Event> getAll() {
        return eventService.getAll();
    }

    @Get("/find/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Event getById(long id) {
        Event event = eventService.getById(id);

        if (event == null)
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "");

        return event;
    }

    @Get("/find/source/{eventSource}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getBySource(String eventSource) {
        try {
            return eventService.getBySource(EventSource.valueOf(eventSource));
        } catch (IllegalArgumentException e) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "");
        }
    }

}
