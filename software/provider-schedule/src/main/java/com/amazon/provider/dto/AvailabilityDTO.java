package com.amazon.provider.dto;

import com.amazon.provider.availability.Availability;

import java.util.UUID;

public class AvailabilityDTO {

    private UUID id;
    private String toDate;
    private String fromDate;
    private String encounterType;

    public AvailabilityDTO(Availability availability) {
        this.id = availability.getId();
        this.fromDate = availability.getFromDate();
        this.toDate = availability.getToDate();
        this.encounterType = availability.getEncounterType().toString();
    }

    public AvailabilityDTO(){

    }

    public UUID getId() {
        return id;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getEncounterType() {
        return encounterType;
    }
}
