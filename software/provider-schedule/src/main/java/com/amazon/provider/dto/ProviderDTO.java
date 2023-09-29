package com.amazon.provider.dto;

import com.amazon.provider.core.Provider;

import java.util.UUID;

public class ProviderDTO {

    public UUID id;
    public String firstName;
    public String lastName;
    public String speciality;
    public String gender;

    public ProviderDTO(Provider provider) {
        this.id = provider.getId();
        this.firstName = provider.getFirstName();
        this.lastName = provider.getLastName();
        this.speciality = provider.getSpeciality();
        this.gender = provider.getGender();
    }

    public ProviderDTO(){

    }
}
