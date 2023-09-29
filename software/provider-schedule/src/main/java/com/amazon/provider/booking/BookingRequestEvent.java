package com.amazon.provider.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingRequestEvent {

    private UUID id;
    private UUID providerAvailabilityId;
    private UUID patientId;

    public BookingRequestEvent(UUID id, UUID providerAvailabilityId, UUID patientId){
        this.id = id;
        this.providerAvailabilityId = providerAvailabilityId;
        this.patientId = patientId;
    }

    public BookingRequestEvent(){}

    public UUID getProviderAvailabilityId() {
        return providerAvailabilityId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "BookingEvent{" +
                "id=" + id +
                ", providerAvailabilityId=" + providerAvailabilityId +
                ", patientId=" + patientId +
                '}';
    }
}
