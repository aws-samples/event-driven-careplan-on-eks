package com.amazon.eda.insuranceprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication

@EnableKafkaStreams
public class InsuranceProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsuranceProcessorApplication.class, args);

		
	}

}
