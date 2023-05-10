package com.outage.repositories;

import com.outage.entities.ElectroHoldEvent;
import io.micronaut.context.annotation.Executable;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ElectroHoldEventRepository extends CrudRepository<ElectroHoldEvent, Long> {
    @Executable
    ElectroHoldEvent findById(long id);

    @Executable
    ElectroHoldEvent findByLastUpdateDate(LocalDateTime lastUpdate);

    @Executable
    ElectroHoldEvent findByCreationDate(LocalDateTime creationDate);

    @Executable
    List<ElectroHoldEvent> findByText(String text);
}
