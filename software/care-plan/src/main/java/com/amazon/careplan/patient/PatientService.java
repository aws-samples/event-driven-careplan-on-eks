package com.amazon.careplan.patient;

import com.amazon.careplan.util.exceptions.CarePlanException;
import com.amazon.careplan.util.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.amazon.careplan.util.exceptions.CarePlanException.ErrorCode.NOT_FOUND;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }
    public Patient getPatient(UUID patientId) {
        return patientRepository.findById(patientId).orElseThrow(() -> new EntityNotFoundException("Could not find patient with id " + patientId));
    }
}
