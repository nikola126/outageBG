package com.outage.entities;

import com.outage.enums.EventSource;
import com.outage.scrapers.ElectroHoldEventDTO;
import com.outage.scrapers.SofiyskaVodaEventDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "event_source")
    private EventSource eventSource;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;
    @Column(name = "removal_date")
    private LocalDateTime removalDate;
    @Column(name = "text", nullable = false, columnDefinition = "TEXT", length = 500)
    private String text;
    @Column(name = "location", nullable = false, columnDefinition = "TEXT", length = 500)
    private String location;
    @Column(name = "additional_text", nullable = false, columnDefinition = "TEXT", length = 500)
    private String additionalText;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;

    public static Event mapToEntity(ElectroHoldEventDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        Event event = new Event();
        event.setEventSource(EventSource.ELECTRO_HOLD);
        event.setCreationDate(now);
        event.setLastUpdateDate(now);

        event.setText(dto.getText());
        event.setLocation(dto.getLocation());
        event.setAdditionalText(dto.getAdditionalText());

        event.setStartDate(dto.getStartTime());
        event.setEndDate(dto.getEndTime());

        return event;
    }

    public static Event mapToEntity(SofiyskaVodaEventDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        Event event = new Event();
        event.setEventSource(EventSource.SOFIYSKA_VODA);
        event.setCreationDate(now);
        event.setLastUpdateDate(now);

        event.setText(dto.getText());
        event.setLocation(dto.getLocation());
        event.setAdditionalText(dto.getAdditionalText());

        event.setStartDate(dto.getStartTime());
        event.setEndDate(dto.getEndTime());

        return event;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", eventSource=" + eventSource +
                ", location='" + location + '\'' +
                ", additionalText='" + additionalText + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
