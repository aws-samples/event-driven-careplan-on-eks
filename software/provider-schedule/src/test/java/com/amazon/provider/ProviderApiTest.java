package com.amazon.provider;

import com.amazon.provider.core.Provider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class ProviderApiTest extends BaseTest{

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void getProvider(){
		var sampleProvider = prepareTestProviderRecord();
		given()
			.mockMvc(mockMvc)
			.when()
			.get("/providers/"+sampleProvider.getId())
			.then()
			.statusCode(200)
			.body("firstName", equalTo(sampleProvider.getFirstName()),
					"lastName", equalTo(sampleProvider.getLastName()),
					"gender", equalTo(sampleProvider.getGender()));
	}

	@Test
	public void returnNotFoundWhenProviderDoesNotExist(){
		given()
				.mockMvc(mockMvc)
				.when()
				.get("/providers/"+ UUID.randomUUID())
				.then()
				.statusCode(404);
	}

	@Test
	public void createProvider() throws JsonProcessingException {
		var sampleProvider = new Provider("Christian", "Müller", "Knee-Specialist", "male");
		var sampleJson = objectMapper.writeValueAsString(sampleProvider);

		given()
				.mockMvc(mockMvc)
				.body(sampleJson)
				.contentType("application/json")
				.when()
				.post("/providers")
				.then()
				.statusCode(201)
				.body("id", notNullValue());
	}

	@Test
	public void deleteProvider() throws JsonProcessingException {
		var sampleProvider = prepareTestProviderRecord();

		given()
				.mockMvc(mockMvc)
				.contentType("application/json")
				.when()
				.delete("/providers/"+sampleProvider.getId())
				.then()
				.statusCode(204);
	}

	@Test
	public void deleteNonExistentProviderReturnsError() throws JsonProcessingException {

		given()
				.mockMvc(mockMvc)
				.contentType("application/json")
				.when()
				.delete("/providers/"+UUID.randomUUID())
				.then()
				.statusCode(404);
	}



}
