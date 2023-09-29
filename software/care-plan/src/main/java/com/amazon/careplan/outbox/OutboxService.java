package com.amazon.careplan.outbox;

import com.amazon.careplan.booking.BookingService;
import com.amazon.careplan.util.exceptions.CarePlanException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.UUID;

@Service
public class OutboxService {

    private final OutboxEntryRepository outboxEntryRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(BookingService.class);

    public OutboxService(OutboxEntryRepository outboxEntryRepository, ObjectMapper objectMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.outboxEntryRepository = outboxEntryRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void publishMessage() {
        var outboxEntryList = outboxEntryRepository.findTop10ByPublishStatus(EventStatus.CREATED.name());
        outboxEntryList.forEach(outboxEvent -> {
            try {
                var event = objectMapper.readTree(outboxEvent.getEventPayload());
                var cloudEvent = CloudEventBuilder.v1()
                        .withId(UUID.randomUUID().toString())
                        .withSource(URI.create("/care-plan"))
                        .withType(outboxEvent.getEventType())
                        .withData(objectMapper.writeValueAsBytes(event))
                        .build();

                kafkaTemplate.send(outboxEvent.getTopic(), outboxEvent.getEventType(), cloudEvent);

                logger.info("Published event on topic {} with type {} and payload {}", outboxEvent.getTopic(), outboxEvent.getEventType(), outboxEvent.getEventPayload());
                outboxEvent.setPublishStatus(EventStatus.PUBLISHED.name());
                outboxEntryRepository.save(outboxEvent);
            } catch (Exception exception) {
                logger.error(exception.getMessage());
            }
        });
    }

    public void saveToOutbox(Object object, String eventType, String topic) {
        try {
            var eventPayload = objectMapper.writeValueAsString(object);
            var outboxEntry = new OutboxEvent(topic, eventType, eventPayload);
            outboxEntryRepository.save(outboxEntry);
        } catch (JsonProcessingException e) {
            throw new CarePlanException(e, CarePlanException.ErrorCode.GENERAL);
        }
    }
}
