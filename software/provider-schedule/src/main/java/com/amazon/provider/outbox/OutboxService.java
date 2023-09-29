package com.amazon.provider.outbox;

import com.amazon.provider.exceptions.ProviderScheduleException;
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
    private final Logger logger = LoggerFactory.getLogger(OutboxService.class);

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
                var availabilityEvent = objectMapper.readTree(outboxEvent.getEventPayload());
                var cloudEvent = CloudEventBuilder.v1()
                        .withId(UUID.randomUUID().toString())
                        .withSource(URI.create("/provider-schedule"))
                        .withType(outboxEvent.getEventType())
                        .withData(objectMapper.writeValueAsBytes(availabilityEvent))
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

    public void saveToOutbox(Object event, String eventType, String topic) {
        try {
            var eventPayload = objectMapper.writeValueAsString(event);
            var outboxEntry = new OutboxEvent(topic, eventType, eventPayload);
            outboxEntryRepository.save(outboxEntry);
        } catch (JsonProcessingException e) {
            throw new ProviderScheduleException(e, ProviderScheduleException.ErrorCode.GENERAL);

        }
    }
}
