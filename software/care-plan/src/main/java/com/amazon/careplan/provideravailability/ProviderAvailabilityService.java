package com.amazon.careplan.provideravailability;

import com.amazon.careplan.util.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProviderAvailabilityService {
    private final ProviderAvailabilityRepository providerAvailabilityRepository;
    private final Logger logger = LoggerFactory.getLogger(ProviderAvailabilityService.class);
    public ProviderAvailabilityService(ProviderAvailabilityRepository providerAvailabilityRepository){
        this.providerAvailabilityRepository = providerAvailabilityRepository;
    }

    public List<ProviderAvailability> getAllProviderAvailability() {
        return providerAvailabilityRepository.findNonBookedAvailabilities();
    }

    public ProviderAvailability getProviderAvailability(UUID providerAvailabilityId) {
        return providerAvailabilityRepository.findById(providerAvailabilityId).orElseThrow(
                () -> new EntityNotFoundException("Provider availability not found: " + providerAvailabilityId)
        );
    }
}
