package com.outage.controller;

import com.outage.entities.ElectroHoldEvent;
import com.outage.services.ElectroHoldEventService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;

@Controller("/electrohold")
public class ElectroHoldEventController {
    @Inject
    ElectroHoldEventService electroHoldEventService;

    @Get("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Iterable<ElectroHoldEvent> getAll() {
        return electroHoldEventService.getAllEvents();
    }

    @Get("/find/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ElectroHoldEvent getById(long id) {
        return electroHoldEventService.getById(id);
    }
}
