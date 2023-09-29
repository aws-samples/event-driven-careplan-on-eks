package com.amazon.careplan.patient;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "patient",schema = "care_plan")
public class Patient {

    @Id
    @GeneratedValue
    public UUID id;
    public String firstName;
    public String lastName;
    public String gender;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateOfBirth;

    public Patient() {
    }

    public Patient( String firstName, String lastName, String gender, LocalDate dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

}
