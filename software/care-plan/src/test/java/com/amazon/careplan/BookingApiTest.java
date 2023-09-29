package com.amazon.careplan;

import com.amazon.careplan.booking.BookingController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


class BookingApiTest extends BaseTest{

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetBooking(){
        var patient = createPatient();
        var booking = createBooking(patient);
        given()
                .mockMvc(mockMvc)
                .when()
                .get("/patients/"+patient.id+ "/bookings")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1));
    }


    @Test
    public void testPostBooking() throws JsonProcessingException, InterruptedException {
        var patient = createPatient();
        var doctorAvailability = createDoctorAvailability();
        var sampleDoctor = new BookingController.BookingRequest(doctorAvailability.getId());
        var sampleJson = objectMapper.writeValueAsString(sampleDoctor);

        given()
                .mockMvc(mockMvc)
                .body(sampleJson)
                .contentType("application/json")
                .when()
                .post("/patients/"+ patient.id + "/bookings")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }


}
