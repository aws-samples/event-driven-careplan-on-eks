package com.amazon.careplan.provideravailability;

import com.amazon.careplan.booking.BookingService;
import com.amazon.careplan.provideravailability.event.ProviderAvailabilityEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import io.cloudevents.CloudEvent;

@Service
public class ProviderAvailabilityListener {

    private final ProviderAvailabilityRepository providerAvailabilityRepository;
    private final BookingService bookingService;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(ProviderAvailabilityListener.class);

    public ProviderAvailabilityListener(ProviderAvailabilityRepository providerAvailabilityRepository, BookingService bookingService, ObjectMapper objectMapper){
        this.providerAvailabilityRepository = providerAvailabilityRepository;
        this.bookingService = bookingService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "provider.availability", groupId ="careplan",containerFactory = "kafkaListenerContainerFactory")
    public void availabilityListener(CloudEvent cloudEvent){
        logger.info("Received message [{}]", cloudEvent.getType());
        var eventPayload = CloudEventUtils
                .mapData(cloudEvent, PojoCloudEventDataMapper.from(objectMapper, ProviderAvailabilityEvent.class));

        if (eventPayload != null) {
            var availabilityEvent = eventPayload.getValue();
            logger.info("Received provider availability of : {}", availabilityEvent.toString());
            switch (cloudEvent.getType()) {
                case "AVAILABILITY_CREATED" ->
                        providerAvailabilityRepository.save(availabilityEvent.toProviderAvailability("CREATED"));
                case "AVAILABILITY_UPDATED" ->
                        providerAvailabilityRepository.save(availabilityEvent.toProviderAvailability("UPDATED"));
                case "AVAILABILITY_DELETED" -> {
                    providerAvailabilityRepository.save(availabilityEvent.toProviderAvailability("DELETED"));
                    bookingService.cancelBooking(availabilityEvent.getAvailability().getId());
                }
            }
        } else
            logger.error("Empty ProviderAvailability payload received {}", cloudEvent.getId());
    }

}
