package com.amazon.careplan.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingReplyEvent {
    private UUID bookingId;
    private UUID availabilityId;
    private String reason;

    public BookingReplyEvent(UUID bookingId, UUID availabilityId, String reason) {
        this.bookingId = bookingId;
        this.availabilityId = availabilityId;
        this.reason = reason;
    }

    public BookingReplyEvent(UUID bookingId, UUID availabilityId) {
        this.bookingId = bookingId;
        this.availabilityId = availabilityId;
    }

    public BookingReplyEvent() {

    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getAvailabilityId() {
        return availabilityId;
    }

    public String getReason() {
        return reason;
    }
}
