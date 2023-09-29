package com.amazon.provider.core;

import com.amazon.provider.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("providers")
@RequestMapping("providers")
public class ProviderController {

    private final ProviderService providerService;
    private final Logger logger = LoggerFactory.getLogger(ProviderController.class);

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<Provider> getProvider(@PathVariable UUID providerId){
        return ResponseEntity.ok(providerService.getProvider(providerId));
    }

    @PostMapping
    public ResponseEntity<Provider> postProvider(@RequestBody Provider provider){
        var savedProvider = providerService.saveProvider(provider);
        return ResponseEntity.status(201).body(savedProvider);
    }

    @DeleteMapping("/{providerId}")
    public ResponseEntity<Provider> postProvider(@PathVariable UUID providerId){
        providerService.deleteProvider(providerId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

}
