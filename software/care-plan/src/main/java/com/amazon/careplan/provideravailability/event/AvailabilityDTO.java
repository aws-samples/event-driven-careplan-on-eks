package com.amazon.careplan.provideravailability.event;

import java.util.UUID;

public class AvailabilityDTO {

    private String fromDate;
    private String toDate;
    private UUID id;

    private String encounterType;

    public AvailabilityDTO(String fromDate, UUID id, String toDate, String encounterType) {
        this.id = id;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.encounterType = encounterType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "ProviderAvailabilityDTO{" +
                "fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                ", id=" + id +
                ", encounterType='" + encounterType + '\'' +
                '}';
    }
}
