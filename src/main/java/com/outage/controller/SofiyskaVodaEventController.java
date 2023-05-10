package com.outage.controller;

import com.outage.entities.SofiyskaVodaEvent;
import com.outage.services.SofiyskaVodaEventService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;

@Controller("/sofiyskavoda")
public class SofiyskaVodaEventController {
    @Inject
    SofiyskaVodaEventService sofiyskaVodaEventService;

    @Get("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Iterable<SofiyskaVodaEvent> getAll() {
        return sofiyskaVodaEventService.getAllEvents();
    }

    @Get("/find/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public SofiyskaVodaEvent getById(long id) {
        return sofiyskaVodaEventService.getById(id);
    }
}
