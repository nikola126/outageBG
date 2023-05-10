package com.outage.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Event {
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
}
