package com.amazon.provider.availability;

import com.amazon.provider.core.Provider;
import com.amazon.provider.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final Logger logger = LoggerFactory.getLogger(AvailabilityController.class);

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/providers/{providerId}/availability")
    public ResponseEntity<List<Availability>> getAvailability(@PathVariable UUID providerId){
        return ResponseEntity.ok(availabilityService.getAvailabilityByProvider(providerId));
    }

    @PostMapping("/providers/{providerId}/availability")
    public ResponseEntity<Availability> postAvailability(@PathVariable String providerId, @RequestBody Availability availability){
        availability.setProviderId(UUID.fromString(providerId));
        var createdAvailability = availabilityService.saveAvailability(availability);
        return ResponseEntity.status(201).body(createdAvailability);
    }

    @DeleteMapping("/providers/{providerId}/availability/{availabilityId}")
    public ResponseEntity<Availability> deleteAvailability(@PathVariable UUID providerId, @PathVariable UUID availabilityId){
        availabilityService.deleteAvailability(providerId, availabilityId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
}
