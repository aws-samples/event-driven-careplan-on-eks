package com.amazon.careplan.booking;

import com.amazon.careplan.outbox.OutboxService;
import com.amazon.careplan.patient.Patient;
import com.amazon.careplan.patient.PatientService;
import com.amazon.careplan.provideravailability.ProviderAvailabilityService;
import com.amazon.careplan.util.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final OutboxService outboxService;
    private final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final PatientService patientService;
    private final ProviderAvailabilityService providerAvailabilityService;

    public BookingService(BookingRepository bookingRepository, OutboxService outboxService, PatientService patientService, ProviderAvailabilityService providerAvailabilityService) {
        this.outboxService = outboxService;
        this.bookingRepository = bookingRepository;
        this.patientService = patientService;
        this.providerAvailabilityService = providerAvailabilityService;
    }

    @Transactional
    public Booking createBooking(UUID providerAvailabilityId, UUID patientId) {
        var booking = new Booking(providerAvailabilityId, patientId);
        bookingRepository.save(booking);
        outboxService.saveToOutbox(booking, "BOOKING_CREATION_REQUEST", "provider.availability.booking.requests");

        return booking;
    }

    @Transactional
    public void confirmBooking(UUID bookingId) {
        var booking = bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new EntityNotFoundException("Couldn't find booking for booking-id " + bookingId));
        booking.confirm();
        bookingRepository.save(booking);
        publishFullEvent(booking, "BOOKING_CONFIRMED");
    }

    @Transactional
    public void rejectBooking(UUID bookingId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find booking for booking-id " + bookingId));
        booking.reject();
        bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(UUID bookingId, UUID patientId) {
        var booking = bookingRepository.findByPatientIdAndId(patientId, bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find booking for booking id " + bookingId));

        bookingRepository.delete(booking);
        outboxService.saveToOutbox(booking, "BOOKING_CANCELLATION_REQUEST", "provider.availability.booking.requests");
    }
    @Transactional
    public void cancelBooking(UUID providerAvailabilityId) {
        var booking = bookingRepository.findBookingByProviderAvailabilityId(providerAvailabilityId);
        booking.ifPresent((it) -> {
            it.cancel();
            bookingRepository.save(it);
            publishFullEvent(it, "BOOKING_CANCELLED");
        });
    }

    public List<Booking> getBookings(UUID patientId){
        return bookingRepository.findAllByPatientId(patientId);
    }

    private void publishFullEvent(Booking booking, String eventType) {
        var patient = patientService.getPatient(booking.getPatientId());
        var providerAvailability = providerAvailabilityService.getProviderAvailability(booking.getProviderAvailabilityId());
        var event = new FullBookingEvent(booking, patient, providerAvailability.getEncounterType());
        outboxService.saveToOutbox(event, eventType, "careplan.bookings");
    }

    private record FullBookingEvent(Booking booking, Patient patient, String encounterType){};

}
