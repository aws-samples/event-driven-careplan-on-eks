package com.amazon.eda.insuranceprocessor.config;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaStreamsConfig {
    public static final String CONFIRMED_BOOKING_TOPIC = "careplan.bookings";
    public static final String ONSITE_BOOKING_TOPIC = "careplan.bookings.onsite";

    @Bean
    NewTopic buildOnsiteBookingTopic() {
        return TopicBuilder.name(ONSITE_BOOKING_TOPIC).partitions(1).replicas(1).build();
    }
}
