package com.amazon.provider.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress
        ));
    }

    @Bean
    public NewTopic availabilityTopic() {
        return new NewTopic("provider.availability", 2, (short) -1);
    }

    @Bean
    public NewTopic bookingRequestTopic() {
        return new NewTopic("provider.availability.booking.requests", 2, (short) -1);
    }
    @Bean
    public NewTopic bookingReplyTopic() {
        return new NewTopic("provider.availability.booking.replies", 2, (short) -1);
    }
}