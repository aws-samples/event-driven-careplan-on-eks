package com.amazon.provider;

import com.amazon.provider.availability.Availability;
import com.amazon.provider.core.Provider;
import com.amazon.provider.core.ProviderRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Ignore;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.BlockingQueue;


@EmbeddedKafka(topics = "availability")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ProviderRepository providerRepository;

    private BlockingQueue<ConsumerRecord<String, Availability>> records;

    private KafkaMessageListenerContainer<String, Availability> container;

    protected Provider prepareTestProviderRecord(){
        var testProvider = new Provider("Max", "Something", "Knees", "Male");
        return providerRepository.save(testProvider);
    }


}
