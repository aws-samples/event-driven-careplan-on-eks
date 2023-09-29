package com.amazon.careplan;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;


class ProviderAvailabilityListenerTest extends BaseTest{

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAvailabilityNotVisibleWhenBooked() {
        var patient = createPatient();
        var booking = createBooking(patient);

        given()
                .mockMvc(mockMvc)
                .contentType("application/json")
                .when()
                .get("/availability")
                .then()
                .statusCode(200)
                .body("findAll{i -> i.id == '"+booking.getProviderAvailabilityId()+"'}", empty());
    }


    @Test
    public void testAvailabilityVisible() {
        given()
                .mockMvc(mockMvc)
                .contentType("application/json")
                .when()
                .get("/availability")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1));
    }



}
