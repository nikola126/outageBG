package com.outage.repositories;

import com.outage.entities.SofiyskaVodaEvent;
import io.micronaut.context.annotation.Executable;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SofiyskaVodaEventRepository extends CrudRepository<SofiyskaVodaEvent, Long> {
    @Executable
    SofiyskaVodaEvent findById(long id);

    @Executable
    SofiyskaVodaEvent findByLastUpdateDate(LocalDateTime lastUpdate);

    @Executable
    SofiyskaVodaEvent findByCreationDate(LocalDateTime creationDate);

    @Executable
    List<SofiyskaVodaEvent> findByText(String text);
}
