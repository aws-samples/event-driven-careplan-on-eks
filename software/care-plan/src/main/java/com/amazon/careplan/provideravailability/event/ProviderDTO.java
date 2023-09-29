package com.amazon.careplan.provideravailability.event;

import java.util.UUID;

public class ProviderDTO {

    private UUID id;
    public String firstName;
    public String lastName;
    public String speciality;
    public String gender;

    public ProviderDTO(UUID id, String firstName, String lastName, String speciality, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.speciality = speciality;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getGender(){
        return gender;
    }

    @Override
    public String toString() {
        return "ProviderDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", speciality='" + speciality + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
