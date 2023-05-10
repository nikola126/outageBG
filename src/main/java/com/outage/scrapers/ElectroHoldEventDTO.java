package com.outage.scrapers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ElectroHoldEventDTO {
    private String text;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String additionalText;

    @Override
    public String toString() {
        return String.format("ElectroHold LOC: %s START: %s END: %s", location, startTime, endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElectroHoldEventDTO that = (ElectroHoldEventDTO) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
