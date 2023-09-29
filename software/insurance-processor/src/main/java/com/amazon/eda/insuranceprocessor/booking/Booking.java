package com.amazon.eda.insuranceprocessor.booking;

import java.util.UUID;


public class Booking {


    private UUID id;
    private UUID providerAvailabilityId;
    private UUID patientId;

    private BookingStatus status = BookingStatus.WAITING_FOR_CONFIRMATION;

    private PaymentMode paymentMode = PaymentMode.INSURANCE_PAY;
    public Booking(UUID providerAvailabilityId, UUID patientId) {
        this.providerAvailabilityId = providerAvailabilityId;
        this.patientId = patientId;
    }

    public Booking() {

    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProviderAvailabilityId(){
        return providerAvailabilityId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void confirm(){
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel(){
        this.status = BookingStatus.CANCELLED_BY_PROVIDER;
    }

    public void reject(){
        this.status = BookingStatus.REJECTED;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public enum BookingStatus {
        WAITING_FOR_CONFIRMATION, CONFIRMED, REJECTED,  CANCELLED_BY_PROVIDER
    }
    public enum PaymentMode {
        SELF_PAY, INSURANCE_PAY
    }
    public PaymentMode getPaymentMode() {
        return paymentMode;
    }
}
