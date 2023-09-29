package com.amazon.careplan.provideravailability.event;

import com.amazon.careplan.provideravailability.ProviderAvailability;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.UUID;

public class ProviderAvailabilityEvent {

    private UUID id;
    private ProviderDTO provider;
    private AvailabilityDTO availability;

    public ProviderAvailabilityEvent(UUID id, ProviderDTO provider, AvailabilityDTO availability) {
        this.id = id;
        this.provider = provider;
        this.availability = availability;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @JsonCreator
    public static ProviderAvailabilityEvent of(@JsonProperty("id") UUID id, @JsonProperty("provider") Map<String, String> provider, @JsonProperty("availability") Map<String, String> availability) {
        var providerDTO = new ProviderDTO(UUID.fromString(provider.get("id")), provider.get("firstName"),
                provider.get("lastName"), provider.get("speciality"), provider.get("gender"));
        var availabilityDTO = new AvailabilityDTO(availability.get("fromDate"),
                UUID.fromString(availability.get("id")),
                availability.get("toDate"), availability.get("encounterType"));

        return new ProviderAvailabilityEvent(id, providerDTO, availabilityDTO);
    }

    public ProviderAvailability toProviderAvailability(String eventType) {
        var providerAvailability = new ProviderAvailability();
        providerAvailability.setId(this.availability.getId());
        providerAvailability.setProviderId(this.provider.getId());
        providerAvailability.setFirstName(this.provider.getFirstName());
        providerAvailability.setLastName(this.provider.getLastName());
        providerAvailability.setSpeciality(this.provider.getSpeciality());
        providerAvailability.setFromDate(this.availability.getFromDate());
        providerAvailability.setToDate(this.availability.getToDate());
        providerAvailability.setEncounterType(this.availability.getEncounterType());
        providerAvailability.setLastEventType(eventType);
        return providerAvailability;
    }
    public ProviderAvailabilityEvent() {

    }

    public ProviderDTO getProvider() {
        return provider;
    }

    public AvailabilityDTO getAvailability() {
        return availability;
    }

    @Override
    public String toString() {
        return "ProviderAvailabilityEvent{" +
                "id=" + id +
                ", provider=" + provider +
                ", availability=" + availability +
                '}';
    }
}
