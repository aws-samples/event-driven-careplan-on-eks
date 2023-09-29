package com.amazon.careplan.provideravailability;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProviderAvailabilityRepository extends CrudRepository<ProviderAvailability, UUID>{

    // Show all provider availability that has not been booked yet or was deleted
    @Query("SELECT pa FROM ProviderAvailability pa LEFT JOIN Booking ap ON ap.providerAvailabilityId = pa.id WHERE ap.id IS NULL and pa.lastEventType != 'DELETED'")
    List<ProviderAvailability> findNonBookedAvailabilities();

}
