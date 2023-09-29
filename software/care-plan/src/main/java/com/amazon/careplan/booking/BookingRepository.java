package com.amazon.careplan.booking;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends CrudRepository<Booking, UUID>{

    List<Booking> findAllByPatientId(UUID patientId);

    Optional<Booking> findByPatientIdAndId(UUID patientId, UUID bookingId);
    Optional<Booking> findBookingByProviderAvailabilityId(UUID providerAvailabilityId);

}
