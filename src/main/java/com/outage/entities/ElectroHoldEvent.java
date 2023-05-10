package com.outage.entities;

import com.outage.scrapers.ElectroHoldEventDTO;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "electro_hold_event")
public class ElectroHoldEvent extends Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;
    @Column(name = "removal_date")
    private LocalDateTime removalDate;

    public static ElectroHoldEvent mapToEntity(ElectroHoldEventDTO dto) {
        ElectroHoldEvent event = new ElectroHoldEvent();

        event.setCreationDate(LocalDateTime.now());
        event.setLastUpdateDate(LocalDateTime.now());
        event.setText(dto.getText());
        event.setAdditionalText(dto.getAdditionalText());
        event.setLocation(dto.getLocation());
        event.setStartDate(dto.getStartTime());
        event.setEndDate(dto.getEndTime());

        return event;
    }

    @Override
    public String toString() {
        return "ElectroHoldEvent{" +
                "creationDate=" + creationDate +
                "location=" + getLocation() +
                '}';
    }
}
