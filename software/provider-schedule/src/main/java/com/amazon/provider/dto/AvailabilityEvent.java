package com.amazon.provider.dto;

import java.util.UUID;

public class AvailabilityEvent {
    private UUID id;
    private ProviderDTO provider;
    private AvailabilityDTO availability;

    public AvailabilityEvent(UUID id, ProviderDTO provider, AvailabilityDTO availability) {
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

    public ProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(ProviderDTO provider) {
        this.provider = provider;
    }

    public AvailabilityDTO getAvailability() {
        return availability;
    }

    public void setAvailability(AvailabilityDTO availability) {
        this.availability = availability;
    }
}
