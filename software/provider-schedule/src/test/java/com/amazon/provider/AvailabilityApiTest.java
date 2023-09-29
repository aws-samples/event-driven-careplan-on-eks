package com.amazon.provider;

import com.amazon.provider.availability.Availability;
import com.amazon.provider.availability.AvailabilityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.notNullValue;


class AvailabilityApiTest extends BaseTest{

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AvailabilityRepository availabilityRepository;

	@Test
	public void testGetAvailability(){
		var testAvailability = prepareTestAvailability();
		var doctorId = testAvailability.get(0).getProviderId();
		given()
			.mockMvc(mockMvc)
			.when()
			.get("/providers/"+doctorId+"/availability")
			.then()
			.statusCode(200);
	}


	@Test
	public void testPostAvailability() throws JsonProcessingException {
		var testProvider = prepareTestProviderRecord();
		var sampleAvailability = new Availability("2023-01-01", "2023-02-01", testProvider.getId(), Availability.EncounterType.ONSITE);
		var sampleJson = objectMapper.writeValueAsString(sampleAvailability);

		given()
				.mockMvc(mockMvc)
				.body(sampleJson)
				.contentType("application/json")
				.when()
				.post("/providers/"+testProvider.getId()+"/availability")
				.then()
				.statusCode(201)
				.body("id", notNullValue());
	}

	@Test
	public void testDeleteAvailability()  {
		var testAvailability = prepareTestAvailability();
		given()
				.mockMvc(mockMvc)
				.contentType("application/json")
				.when()
				.delete("/providers/"+testAvailability.get(0).getProviderId()+"/availability/"+testAvailability.get(0).getId())
				.then()
				.statusCode(204);
	}

	@Test
	public void shouldReturnErrorWhenDeleteNotPresentAvailability()  {
		var testProvider = prepareTestProviderRecord();
		given()
				.mockMvc(mockMvc)
				.contentType("application/json")
				.when()
				.delete("/providers/"+testProvider.getId()+"/availability/"+ UUID.randomUUID())
				.then()
				.statusCode(404);
	}


	protected List<Availability> prepareTestAvailability(){
		var testProvider = prepareTestProviderRecord();
		var availability = new Availability("01-01-2023", "01-01-2023", testProvider.getId(), Availability.EncounterType.ONSITE);
		var availability2 = new Availability("01-01-2024", "01-01-2024", testProvider.getId(), Availability.EncounterType.VIRTUAL);
		availabilityRepository.saveAll(List.of(availability, availability2));
		return List.of(availability, availability2);
	}

}
