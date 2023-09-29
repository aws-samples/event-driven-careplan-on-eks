package com.amazon.eda.insuranceprocessor.patient;


import java.util.Date;
import java.util.UUID;


public class Patient {


    public UUID id;
    public String firstName;
    public String lastName;
    public String gender;

    public Date dateOfBirth;




    public Patient() {
    }

    public Patient(String firstName, String lastName, String gender,Date dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

}
