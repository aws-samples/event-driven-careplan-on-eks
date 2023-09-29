package com.amazon.careplan.patient;

import com.amazon.careplan.util.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final Logger logger = LoggerFactory.getLogger(PatientController.class);

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<Patient> getPatient(@PathVariable UUID patientId){
        return ResponseEntity.ok(patientService.getPatient(patientId));
    }

    @PostMapping
    public ResponseEntity<Patient> postPatient(@RequestBody Patient patient){
        patientService.savePatient(patient);
        return ResponseEntity.status(201).body(patient);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
}
