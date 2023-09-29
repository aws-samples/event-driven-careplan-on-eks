package com.amazon.careplan.provideravailability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProviderAvailabilityController {

    private final ProviderAvailabilityService providerAvailabilityService;
    private final Logger logger = LoggerFactory.getLogger(ProviderAvailabilityController.class);

    public ProviderAvailabilityController(ProviderAvailabilityService providerAvailabilityService) {
        this.providerAvailabilityService = providerAvailabilityService;
    }

    @GetMapping("/availability")
    public ResponseEntity<List<ProviderAvailability>> getAvailability(){
        return ResponseEntity.ok(providerAvailabilityService.getAllProviderAvailability());
    }

}
