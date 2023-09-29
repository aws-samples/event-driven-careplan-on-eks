package com.amazon.careplan.provideravailability;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "provider_availability", schema = "care_plan")
public class ProviderAvailability {

    @Id
    private UUID id;
    private String fromDate;
    private String toDate;
    private UUID providerId;
    private String lastName;
    private String firstName;
    private String speciality;
    private String gender;
    private String encounterType;
    private String lastEventType;
    public ProviderAvailability(){

    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public void setLastEventType(String lastEventType) {
        this.lastEventType = lastEventType;
    }
    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    @JsonProperty("provider")
    private void unpackNestedProvider(Map<String, String> provider){
        this.providerId = UUID.fromString((String) provider.get("id"));
        this.firstName = provider.get("firstName");
        this.lastName = provider.get("lastName");
        this.speciality = provider.get("speciality");
    }

    @JsonProperty("availability")
    private void unpackNestedAvailability(Map<String, String> availability){
        this.id = UUID.fromString(availability.get("id"));
        this.fromDate = availability.get("fromDate");
        this.toDate = availability.get("toDate");
        this.encounterType = availability.get("encounterType");
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEncounterType() {
        return encounterType;
    }


}
