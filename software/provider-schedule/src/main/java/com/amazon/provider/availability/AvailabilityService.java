package com.amazon.provider.availability;

import com.amazon.provider.booking.BookingReplyEvent;
import com.amazon.provider.core.ProviderRepository;
import com.amazon.provider.dto.AvailabilityDTO;
import com.amazon.provider.dto.AvailabilityEvent;
import com.amazon.provider.dto.ProviderDTO;
import com.amazon.provider.exceptions.EntityNotFoundException;
import com.amazon.provider.outbox.OutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final ProviderRepository providerRepository;

    private final Logger logger = LoggerFactory.getLogger(AvailabilityService.class);
    private final OutboxService outboxService;
    public AvailabilityService(AvailabilityRepository availabilityRepository, OutboxService outboxService, ProviderRepository providerRepository){
        this.availabilityRepository = availabilityRepository;
        this.outboxService = outboxService;
        this.providerRepository = providerRepository;
    }

    @Transactional
    public Availability saveAvailability(Availability availability) {
        return saveAndPublishAvailability(availability, PublishingType.AVAILABILITY_CREATED);
    }

    @Transactional
    public void deleteAvailability(UUID providerId, UUID availabilityId) {
        var availabilityList = availabilityRepository.findByIdAndProviderId(availabilityId, providerId);
        if(!availabilityList.isEmpty()) {
            deleteAndPublishAvailability(availabilityList.get(0));
        } else {
            throw new EntityNotFoundException("Combination of ProviderId and AvailabilityId not found");
        }
    }

    @Transactional
    public void bookAvailability(UUID availabilityId, UUID bookingReferenceId) {
        try {
            var availability = availabilityRepository
                    .findById(availabilityId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Availability with id %s not found.", availabilityId)));


            if (availability.getBookingReferenceId() != null) {
                throw new IllegalStateException("Availability is already booked.");
            }

            availability.book(bookingReferenceId);

            saveAndPublishAvailability(availability, PublishingType.AVAILABILITY_UPDATED);

            var bookingReplyEvent = new BookingReplyEvent(bookingReferenceId, availabilityId);

            sendBookingReply(bookingReplyEvent, PublishingType.BOOKING_REQUEST_CONFIRMED);
        } catch (Exception e) {
            logger.error("Error while processing booking request", e);
            var bookingReplyEvent = new BookingReplyEvent(bookingReferenceId, availabilityId, e.getMessage() );
            sendBookingReply(bookingReplyEvent, PublishingType.BOOKING_REQUEST_REJECTED);
        }
    }

    @Transactional
    public void cancelAvailability(UUID availabilityId) {
        var availability = availabilityRepository.findById(availabilityId);

        availability.ifPresent((it) -> {
            it.cancel();
            saveAndPublishAvailability(it, PublishingType.AVAILABILITY_UPDATED);
        });
    }

    public List<Availability> getAvailabilityByProvider(UUID providerId) {
        return availabilityRepository.findByProviderId(providerId);
    }

    private Availability saveAndPublishAvailability(Availability availability, PublishingType publishingType) {
        availabilityRepository.save(availability);
        publishAvailabilityEvent(availability, publishingType);

        return availability;
    }

    private Availability deleteAndPublishAvailability(Availability availability) {
        availabilityRepository.delete(availability);
        publishAvailabilityEvent(availability, PublishingType.AVAILABILITY_DELETED);

        return availability;
    }

    private void publishAvailabilityEvent(Availability availability, PublishingType publishingType) {
        var provider = providerRepository
                .findById(availability.getProviderId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Provider with id %s not found.", availability.getProviderId())));
        var providerDTO = new ProviderDTO(provider);
        var availabilityDTO = new AvailabilityDTO(availability);
        var availabilityEvent = new AvailabilityEvent(UUID.randomUUID(), providerDTO,availabilityDTO);
        outboxService.saveToOutbox(availabilityEvent, publishingType.name(), "provider.availability");
    }

    private void sendBookingReply(BookingReplyEvent bookingReplyEvent, PublishingType publishingType) {
        outboxService.saveToOutbox(bookingReplyEvent, publishingType.name(), "provider.availability.booking.replies");
    }

    private enum PublishingType{
        AVAILABILITY_CREATED, AVAILABILITY_UPDATED, AVAILABILITY_DELETED, BOOKING_REQUEST_CONFIRMED, BOOKING_REQUEST_REJECTED
    }
}
