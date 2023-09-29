package com.amazon.eda.insuranceprocessor.processor;

import com.amazon.eda.insuranceprocessor.booking.Booking;
import com.amazon.eda.insuranceprocessor.config.KafkaStreamsConfig;

import com.amazon.eda.insuranceprocessor.patient.Patient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import io.cloudevents.kafka.CloudEventDeserializer;
import io.cloudevents.kafka.CloudEventSerializer;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;

import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class EventStreamProcessor {

    private final Logger logger = LoggerFactory.getLogger(EventStreamProcessor.class);
    private static final Serde<CloudEvent> cloudEventSerde;
    private static final CloudEventSerializer serializer = new CloudEventSerializer();
    private static final CloudEventDeserializer deserializer = new CloudEventDeserializer();

    static {
        Map<String, Object> ceSerializerConfigs = Map.of();
        serializer.configure(ceSerializerConfigs, false);
        deserializer.configure(ceSerializerConfigs, false);
        cloudEventSerde = Serdes.serdeFrom(serializer, deserializer);
    }

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {


        KStream<String, CloudEvent> kStream =
                streamsBuilder.stream(KafkaStreamsConfig.CONFIRMED_BOOKING_TOPIC, Consumed.with(Serdes.String(), cloudEventSerde));


        kStream.filter((key, value) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    var eventPayload = CloudEventUtils
                            .mapData(value, PojoCloudEventDataMapper.from(objectMapper, FullBookingEvent.class));
                    FullBookingEvent fullBookingEvent = eventPayload.getValue();
                    return (fullBookingEvent.booking().getPaymentMode() == Booking.PaymentMode.INSURANCE_PAY
                            && value.getType().equals("BOOKING_CONFIRMED"));
                })
                .mapValues((key, value) -> {

                    logger.info("Routing insurance pay event to onsite booking topic ...");
                    return value;
                })
                .peek((key, value) -> System.out.println("The key is : " + key + "----" + value))
                .to(KafkaStreamsConfig.ONSITE_BOOKING_TOPIC, Produced.with(Serdes.String(), cloudEventSerde));
    }

    private record FullBookingEvent(Booking booking, Patient patient, String encounterType) {
    }

    ;

}
