package com.amazon.provider.core;

import com.amazon.provider.exceptions.EntityNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository){
        this.providerRepository = providerRepository;
    }

    public Provider saveProvider(Provider provider) {
        return providerRepository.save(provider);
    }

    public Provider getProvider(UUID providerId) {
        return providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Provider with id %s not found.", providerId)));
    }

    public void deleteProvider(UUID providerId) {
        try {
            providerRepository.deleteById(providerId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(String.format("Provider with id %s not found.", providerId));
        }
    }
}
