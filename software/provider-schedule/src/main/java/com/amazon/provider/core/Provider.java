package com.amazon.provider.core;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "provider", schema = "provider_schedule")
public class Provider {

    @Id
    @GeneratedValue
    private UUID id;
    private String firstName;
    private String lastName;
    private String speciality;
    private String gender;

    public Provider(String firstName, String lastName, String speciality, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.speciality = speciality;
        this.gender = gender;
    }

    public Provider() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getGender() {
        return gender;
    }

}
