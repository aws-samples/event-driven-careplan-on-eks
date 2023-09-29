package com.amazon.careplan.booking;

import com.amazon.careplan.util.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class BookingController {

    private final BookingService bookingService;
    private final Logger logger = LoggerFactory.getLogger(BookingController.class);

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/patients/{patientId}/bookings")
    public ResponseEntity<List<Booking>> getBookings(@PathVariable UUID patientId){
        var bookings = bookingService.getBookings(patientId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/patients/{patientId}/bookings")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest bookingRequest, @PathVariable UUID patientId){
        var booking = bookingService.createBooking(bookingRequest.providerAvailabilityId, patientId);
        return ResponseEntity.status(201).body(booking);
    }

    @DeleteMapping("/patients/{patientId}/bookings/{bookingId}")
    public ResponseEntity<Booking> deleteBooking(@PathVariable UUID patientId, @PathVariable UUID bookingId){
        bookingService.deleteBooking(bookingId, patientId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    public record BookingRequest(UUID providerAvailabilityId){}
}
