package com.outage.entities;

import com.outage.scrapers.SofiyskaVodaEventDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "sofyiska_voda_event")
public class SofiyskaVodaEvent extends Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;
    @Column(name = "removal_date")
    private LocalDateTime removalDate;

    public static SofiyskaVodaEvent mapToEntity(SofiyskaVodaEventDTO dto) {
        SofiyskaVodaEvent event = new SofiyskaVodaEvent();

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
        return "SofiyskaVodaEvent{" +
                "creationDate=" + creationDate +
                "location=" + getLocation() +
                '}';
    }
}
