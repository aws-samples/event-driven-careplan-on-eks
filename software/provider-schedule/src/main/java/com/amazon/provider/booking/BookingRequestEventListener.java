package com.amazon.provider.booking;

import com.amazon.provider.availability.AvailabilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookingRequestEventListener {

    private final AvailabilityService availabilityService;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(BookingRequestEventListener.class);

    public BookingRequestEventListener(AvailabilityService availabilityService, ObjectMapper objectMapper){
        this.availabilityService = availabilityService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "provider.availability.booking.requests", groupId ="providerschedule",containerFactory = "kafkaListenerContainerFactory")
    public void bookingListener(CloudEvent cloudEvent){
        logger.info("Received message [{}]", cloudEvent.getType());
        var eventPayload = CloudEventUtils
                .mapData(cloudEvent, PojoCloudEventDataMapper.from(objectMapper, BookingRequestEvent.class));
        if (eventPayload != null) {
            var bookingEvent = eventPayload.getValue();
            logger.info("Received booking of : {}", bookingEvent);
            switch (cloudEvent.getType()) {
                case "BOOKING_CREATION_REQUEST" ->
                        availabilityService.bookAvailability(bookingEvent.getProviderAvailabilityId(), bookingEvent.getId());
                case "BOOKING_CANCELLATION_REQUEST" ->
                        availabilityService.cancelAvailability(bookingEvent.getProviderAvailabilityId());
            }
        } else {
            logger.error("Empty Booking Request payload received {}", cloudEvent.getId());
        }
    }

}
