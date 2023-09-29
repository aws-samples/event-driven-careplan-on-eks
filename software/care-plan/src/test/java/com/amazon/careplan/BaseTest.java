package com.amazon.careplan;

import com.amazon.careplan.booking.Booking;
import com.amazon.careplan.booking.BookingRepository;
import com.amazon.careplan.provideravailability.ProviderAvailability;
import com.amazon.careplan.provideravailability.ProviderAvailabilityRepository;
import com.amazon.careplan.patient.Patient;
import com.amazon.careplan.patient.PatientRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;


@EmbeddedKafka(topics = "bookings")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private BlockingQueue<ConsumerRecord<String, Booking>> records;

    private KafkaMessageListenerContainer<String, Booking> container;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ProviderAvailabilityRepository providerAvailabilityRepository;

    @Autowired
    private BookingRepository bookingRepository;

    protected Patient createPatient(){
        var patient = new Patient("Mithun", "EDA", "Male", LocalDate.now());
        return patientRepository.save(patient);
    }

    protected ProviderAvailability createDoctorAvailability(){
        var doctorAvailability = new ProviderAvailability();

        doctorAvailability.setId(UUID.randomUUID());
        doctorAvailability.setFirstName("Max");
        doctorAvailability.setLastName("EDA");
        return providerAvailabilityRepository.save(doctorAvailability);
    }

    protected Booking createBooking(Patient patient){
        var doctorAvailability = createDoctorAvailability();
        var booking = new Booking(doctorAvailability.getId(), patient.id);
        return bookingRepository.save(booking);
    }

}
