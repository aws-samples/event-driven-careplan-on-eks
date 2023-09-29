package com.amazon.careplan.booking;

import com.amazon.careplan.provideravailability.ProviderAvailabilityRepository;
import com.amazon.careplan.provideravailability.event.ProviderAvailabilityEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookingReplyListener {

    private final BookingService bookingService;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(BookingReplyListener.class);

    public BookingReplyListener(BookingService bookingService, ObjectMapper objectMapper){
        this.bookingService = bookingService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "provider.availability.booking.replies", groupId ="careplan",containerFactory = "kafkaListenerContainerFactory")
    public void availabilityListener(CloudEvent cloudEvent){
        logger.info("Received message [{}]", cloudEvent.getType());
        var eventPayload = CloudEventUtils
                .mapData(cloudEvent, PojoCloudEventDataMapper.from(objectMapper, BookingReplyEvent.class));

        if (eventPayload != null) {
            var bookingReplyEvent = eventPayload.getValue();
            logger.info("Received provider booking reply of : {}", bookingReplyEvent.toString());
            switch (cloudEvent.getType()) {
                case "BOOKING_REQUEST_CONFIRMED" ->
                        bookingService.confirmBooking(bookingReplyEvent.getBookingId());
                case "BOOKING_REQUEST_REJECTED" ->
                        bookingService.rejectBooking(bookingReplyEvent.getBookingId());
            }
        } else
            logger.error("Empty Booking Reply payload received {}", cloudEvent.getId());
    }

}
