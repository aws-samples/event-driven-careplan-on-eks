package com.amazon.provider.availability;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "availability", schema = "provider_schedule")
public class Availability {

    @Id
    @GeneratedValue
    private UUID id;
    private String fromDate;
    private String toDate;
    private UUID providerId;
    private UUID bookingReferenceId;
    @Enumerated(EnumType.STRING)
    private EncounterType encounterType = EncounterType.NOT_SELECTED;

    public Availability(String fromDate, String toDate, UUID providerId, EncounterType encounterType) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.providerId = providerId;
        this.encounterType = encounterType;
    }

    public Availability(){

    }

    public UUID getId() {
        return id;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public void book(UUID patientId) {
        this.bookingReferenceId = patientId;
    }
    public void cancel() {
        this.bookingReferenceId = null;
    }

    public UUID getBookingReferenceId() {
        return bookingReferenceId;
    }

    public EncounterType getEncounterType() {
        return encounterType;
    }

    public enum EncounterType{
        NOT_SELECTED, ONSITE, VIRTUAL
    }
}
