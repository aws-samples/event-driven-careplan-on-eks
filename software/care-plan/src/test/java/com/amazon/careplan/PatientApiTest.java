package com.amazon.careplan;

import com.amazon.careplan.patient.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class PatientApiTest extends BaseTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getPatients(){
        var patient = createPatient();
        given()
                .mockMvc(mockMvc)
                .when()
                .get("/patients/"+ patient.id)
                .then()
                .statusCode(200)
                .body("firstName", equalTo(patient.firstName),
                        "lastName", equalTo(patient.lastName));
    }

    @Test
    public void testCreatePatient() throws JsonProcessingException {
        var sampleDoctor = new Patient("John", "Smith", "Male", LocalDate.of(2000,10,20));
        var sampleJson = objectMapper.writeValueAsString(sampleDoctor);

        given()
                .mockMvc(mockMvc)
                .body(sampleJson)
                .contentType("application/json")
                .when()
                .post("/patients")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }


}
