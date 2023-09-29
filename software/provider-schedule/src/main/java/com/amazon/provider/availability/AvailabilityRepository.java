package com.amazon.provider.availability;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends CrudRepository<Availability, UUID>{

    List<Availability> findByProviderId(UUID providerId);
    List<Availability> findByIdAndProviderId(UUID id, UUID providerId);

}
