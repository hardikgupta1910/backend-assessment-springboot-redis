package com.Hardik.Backend_Assesment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendAssesmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendAssesmentApplication.class, args);
	}

}
